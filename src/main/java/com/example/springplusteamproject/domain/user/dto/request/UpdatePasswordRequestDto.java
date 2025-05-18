package com.example.springplusteamproject.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotBlank
    @JsonProperty("oldPassword")
    private String oldPassword;

    @NotBlank
    @JsonProperty("newPassword")
    private String newPassword;
}
