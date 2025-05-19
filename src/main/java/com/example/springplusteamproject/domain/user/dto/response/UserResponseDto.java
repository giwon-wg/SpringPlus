package com.example.springplusteamproject.domain.user.dto.response;

import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String address;
    private String phone;
    private UserRole userRole;

    public static UserResponseDto from(User findUser) {
        return UserResponseDto.builder()
            .id(findUser.getId())
            .email(findUser.getEmail())
            .nickname(findUser.getNickname())
            .address(findUser.getAddress())
            .phone(findUser.getPhone())
            .userRole(findUser.getUserRole())
            .build();
    }
}
