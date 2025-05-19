package com.example.springplusteamproject.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequestDto {

    @Schema(description = "현재 비밀번호", example = "oldpassword123")
    @NotBlank
    @JsonProperty("oldPassword")
    private String oldPassword;

    @Schema(description = "새 비밀번호", example = "newpassword123")
    @NotBlank
    @JsonProperty("newPassword")
    private String newPassword;
}
