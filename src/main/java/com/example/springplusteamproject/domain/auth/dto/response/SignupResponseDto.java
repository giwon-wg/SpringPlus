package com.example.springplusteamproject.domain.auth.dto.response;

import com.example.springplusteamproject.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    private long id;
    private String nickname;
    private String email;

    public static SignupResponseDto from(User savedUser) {
        return new SignupResponseDto(
            savedUser.getId(),
            savedUser.getNickname(),
            savedUser.getEmail()
        );
    }

}
