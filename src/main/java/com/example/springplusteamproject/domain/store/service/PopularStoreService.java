package com.example.springplusteamproject.domain.store.service;

import java.util.List;

import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;

public interface PopularStoreService {

    List<StoreResponseDto> getPopularStoresByView();

    List<StoreResponseDto> getPopularStoresFromCache();
}
