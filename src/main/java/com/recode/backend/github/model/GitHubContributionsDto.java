package com.recode.backend.github.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitHubContributionsDto {
    private int thisYearContributions;     // 올해 총 기여수
    private int longestStreak;             // 최장 연속 기여일
    private int thisWeekContributions;     // 이번주 기여수
}

