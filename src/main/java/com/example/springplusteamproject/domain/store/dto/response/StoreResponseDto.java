package com.example.springplusteamproject.domain.store.dto.response;

import java.time.LocalTime;

import com.example.springplusteamproject.domain.store.entity.Store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreResponseDto {

    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private String image;

    private Long minOrderPrice;

    private LocalTime openTime;

    private LocalTime closeTime;

    public static StoreResponseDto fromEntity(Store store) {
        return StoreResponseDto.builder()
            .id(store.getId())
            .name(store.getName())
            .address(store.getAddress())
            .phoneNumber(store.getPhoneNumber())
            .image(store.getImage())
            .minOrderPrice(store.getMinOrderPrice())
            .openTime(store.getOpenTime())
            .closeTime(store.getCloseTime())
            .build();
    }
}
