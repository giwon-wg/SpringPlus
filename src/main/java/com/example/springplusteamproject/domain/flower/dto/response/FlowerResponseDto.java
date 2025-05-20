package com.example.springplusteamproject.domain.flower.dto.response;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class FlowerResponseDto {

    @Getter
    @AllArgsConstructor
    public static class Create {

        private Long id;

        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Get {

        private Long id;

        private String name;

        private String description;

        private Type type;

        private Color color;

        private Season season;

        private int price;

        private int stock;

        private LocalDate expirationDate;

        public static Get toDto(Flower flower) {
            return Get.builder()
                    .id(flower.getId())
                .name(flower.getName())
                .description(flower.getDescription())
                .type(flower.getType())
                .color(flower.getColor())
                .season(flower.getSeason())
                .price(flower.getPrice())
                .stock(flower.getStock())
                .expirationDate(flower.getExpirationDate())
                .build();
        }
    }
}
