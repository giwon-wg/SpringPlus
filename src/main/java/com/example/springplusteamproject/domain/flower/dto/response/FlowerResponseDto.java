package com.example.springplusteamproject.domain.flower.dto.response;

import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class FlowerResponseDto {

    @Getter
    @AllArgsConstructor
    public static class Create {

        private Long id;

        private String name;
    }

    @Getter
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

        private LocalDateTime expirationDate;
    }
}
