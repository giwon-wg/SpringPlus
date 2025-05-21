package com.example.springplusteamproject.domain.store.dto.response;

import java.time.LocalTime;

import com.example.springplusteamproject.domain.store.entity.Store;

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

    public static StoreListResponseDto fromEntity(Store store) {
        return StoreListResponseDto.builder()
            .id(store.getId())
            .name(store.getName())
            .image(store.getImage())
            .minOrderPrice(store.getMinOrderPrice())
            .openTime(store.getOpenTime())
            .closeTime(store.getCloseTime())
            .build();
    }
}
