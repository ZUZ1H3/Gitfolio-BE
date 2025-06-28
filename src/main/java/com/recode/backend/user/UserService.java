package com.recode.backend.user;

import com.recode.backend.auth.model.OAuth2UserInfo;
import com.recode.backend.user.model.User;
import com.recode.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveOrUpdate(OAuth2UserInfo userInfo) {
        Optional<User> optionalUser = userRepository.findByLogin(userInfo.getLogin());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.update(userInfo.getName(), userInfo.getEmail(), userInfo.getAvatarUrl());
            log.info("🔄 기존 사용자 정보 업데이트: {}", existingUser);
            return userRepository.save(existingUser);
        }else {
            User newUser = User.builder()
                    .login(userInfo.getLogin())
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .avatarUrl(userInfo.getAvatarUrl())
                    .build();
            log.info("🆕 신규 사용자 저장: {}", newUser);
            return userRepository.save(newUser);
        }
    }
}