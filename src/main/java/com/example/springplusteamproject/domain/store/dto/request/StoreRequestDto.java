package com.example.springplusteamproject.domain.store.dto.request;

import lombok.Getter;

@Getter
public class StoreRequestDto {

    private String name;

    private String address;

    private String image;

    private String phoneNumber;

    private Long minOrderPrice;

    private String openTime;

    private String closeTime;

}
