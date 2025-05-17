package com.example.springplusteamproject.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    String nickname;

    @NotBlank(message = "주소는 필수입니다.")
    String address;

    @NotBlank(message = "전화번호는 필수입니다.")
    String phone;

    @NotBlank(message = "유저 역할은 필수입니다.")
    @JsonProperty("userRole")
    String userRole;

    String profileImage;

    String brn;

    @AssertTrue(message = "OWNER는 사업자 등록번호(brn)을 반드시 입력해야 합니다.")
    public boolean isBrnValidForOwner() {
        if ("OWNER".equalsIgnoreCase(userRole)) {
            return brn != null && !brn.trim().isEmpty();
        }
        return true; // CUSTOMER인 경우 무조건 true
    }
}
