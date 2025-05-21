package com.example.springplusteamproject.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class StoreCheckNameRequestDto {

    @Schema(description = "가게 이름", example = "이쁜화원")
    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

}
