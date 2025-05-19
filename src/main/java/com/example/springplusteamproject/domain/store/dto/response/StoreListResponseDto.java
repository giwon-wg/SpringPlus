package com.example.springplusteamproject.domain.store.dto.response;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreListResponseDto {

    private Long id;

    private String name;

    private String image;

    private Long minOrderPrice;

    private LocalTime openTime;

    private LocalTime closeTime;
}
