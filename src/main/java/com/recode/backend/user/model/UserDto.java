package com.recode.backend.user.model;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class UserDto {
    private String login;
    private String name;
    private String email;
    private String avatarUrl;


    public static UserDto create(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .login(user.getLogin())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

}
