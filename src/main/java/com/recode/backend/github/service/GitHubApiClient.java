package com.recode.backend.github.service;

import com.recode.backend.github.model.DayContribution;
import com.recode.backend.github.model.GitHubContributionsDto;
import com.recode.backend.github.model.GitHubProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubProfileDto getUserProfile(String accessToken) {
        String url = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        GitHubProfileDto dto = new GitHubProfileDto();
        dto.setName((String) body.get("name"));
        dto.setLogin((String) body.get("login"));
        dto.setFollowers((Integer) body.get("followers"));
        dto.setFollowing((Integer) body.get("following"));
        dto.setPublicRepos((Integer) body.get("public_repos"));

        return dto;
    }

    public GitHubContributionsDto getUserContributions(String accessToken, String username) {
        // GraphQL 쿼리로 기여도 데이터 조회
        String graphqlQuery = buildContributionsQuery(username);
        Map<String, Object> graphqlResponse = executeGraphQLQuery(accessToken, graphqlQuery);

        return parseContributionsResponse(graphqlResponse);
    }

    private String buildContributionsQuery(String username) {
        return """
            {
              user(login: "%s") {
                contributionsCollection {
                  contributionCalendar {
                    totalContributions
                    weeks {
                      contributionDays {
                        date
                        contributionCount
                      }
                    }
                  }
                }
              }
            }
            """.formatted(username);
    }

    private Map<String, Object> executeGraphQLQuery(String accessToken, String query) {
        String url = "https://api.github.com/graphql";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of("query", query);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    private GitHubContributionsDto parseContributionsResponse(Map<String, Object> response) {
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            Map<String, Object> user = (Map<String, Object>) data.get("user");
            Map<String, Object> contributionsCollection = (Map<String, Object>) user.get("contributionsCollection");
            Map<String, Object> contributionCalendar = (Map<String, Object>) contributionsCollection.get("contributionCalendar");

            int totalContributions = (Integer) contributionCalendar.get("totalContributions");
            List<Map<String, Object>> weeks = (List<Map<String, Object>>) contributionCalendar.get("weeks");

            // 모든 날짜별 기여도 데이터 수집
            List<DayContribution> allDays = new ArrayList<>();
            for (Map<String, Object> week : weeks) {
                List<Map<String, Object>> days = (List<Map<String, Object>>) week.get("contributionDays");
                for (Map<String, Object> day : days) {
                    String dateStr = (String) day.get("date");
                    int count = (Integer) day.get("contributionCount");
                    allDays.add(new DayContribution(LocalDate.parse(dateStr), count));
                }
            }

            // 통계 계산
            int longestStreak = calculateLongestStreak(allDays);
            int thisWeekContributions = calculateThisWeekContributions(allDays);

            return new GitHubContributionsDto(
                    totalContributions,
                    longestStreak,
                    thisWeekContributions
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GitHub contributions data", e);
        }
    }

    private int calculateLongestStreak(List<DayContribution> days) {
        int maxStreak = 0;
        int currentStreak = 0;

        for (DayContribution day : days) {
            if (day.getContributionCount() > 0) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }

        return maxStreak;
    }

    private int calculateThisWeekContributions(List<DayContribution> days) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        return days.stream()
                .filter(day -> !day.getDate().isBefore(weekStart) && !day.getDate().isAfter(today))
                .mapToInt(DayContribution::getContributionCount)
                .sum();
    }
}
