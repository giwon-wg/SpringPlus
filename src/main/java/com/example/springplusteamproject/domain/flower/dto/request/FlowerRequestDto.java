package com.example.springplusteamproject.domain.flower.dto.request;

import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class FlowerRequestDto {

    @Getter
    @AllArgsConstructor
    public static class Create {

        @Schema(description = "꽃 이름", example = "자나장미")
        @NotBlank(message = "꽃 이름은 필수입니다.")
        private String name;

        @Schema(description = "꽃말", example = "영원한 사랑, 끝없는 사랑, 행복한 사랑")
        @NotBlank(message = "꽃말은 필수입니다.")
        private String description;

        @Schema(description = "꽃 종류", example = "ROSE")
        @NotBlank(message = "꽃 종류는 필수입니다.")
        private Type type;

        @Schema(description = "꽃 색상", example = "PINK")
        @NotBlank(message = "색상은 필수입니다.")
        private Color color;

        @Schema(description = "개화 시기", example = "ALL")
        @NotBlank(message = "개화 시기는 필수입니다.")
        private Season season;

        @Schema(description = "가격", example = "700")
        @NotBlank(message = "가격은 필수입니다.")
        private int price;

        @Schema(description = "재고", example = "100")
        @NotBlank(message = "재고는 필수입니다.")
        private int stock;

        @Schema(description = "입고일", example = "2025-05-19")
        @NotBlank(message = "입고일은 필수입니다.")
        private LocalDateTime expirationDate;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {

        @Schema(description = "꽃 이름", example = "자나장미")
        private String name;

        @Schema(description = "꽃말", example = "영원한 사랑, 끝없는 사랑, 행복한 사랑")
        private String description;

        @Schema(description = "꽃 종류", example = "ROSE")
        private Type type;

        @Schema(description = "꽃 색상", example = "PINK")
        private Color color;

        @Schema(description = "개화 시기", example = "ALL")
        private Season season;

        @Schema(description = "가격", example = "700")
        private int price;

        @Schema(description = "재고", example = "100")
        private int stock;
    }

}
