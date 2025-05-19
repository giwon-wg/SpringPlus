package com.example.springplusteamproject.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(description = "이메일", example = "email@email.com")
    @NotBlank(message = "이메일은 반드시 입력해야 합니다.")
    @Email
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    private String password;

}
