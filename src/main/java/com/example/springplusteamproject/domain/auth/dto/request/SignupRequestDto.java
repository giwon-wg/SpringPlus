package com.example.springplusteamproject.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @Schema(description = "이메일", example = "email@email.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    String email;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password;

    @Schema(description = "닉네임", example = "아기다람쥐")
    @NotBlank(message = "닉네임은 필수입니다.")
    String nickname;

    @Schema(description = "주소", example = "서울특별시")
    @NotBlank(message = "주소는 필수입니다.")
    String address;

    @Schema(description = "전화번호", example = "010-1111-9999")
    @NotBlank(message = "전화번호는 필수입니다.")
    String phone;

    @Schema(description = "유저 역할", example = "CUSTOMER / OWNER")
    @NotBlank(message = "유저 역할은 필수입니다.")
    @JsonProperty("userRole")
    String userRole;

    @Schema(description = "프로필 이미지", example = "프로필 이미지 링크")
    String profileImage;

    @Schema(description = "사업자 등록번호", example = "123-13-13334")
    String brn;

}
