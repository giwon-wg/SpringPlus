package com.example.springplusteamproject.domain.store.service;

import java.util.List;

import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;

public interface StoreService{

    StoreResponseDto createStore(StoreRequestDto dto);

    void deleteStore(Long id);

    List<StoreListResponseDto> getAllStores();

    StoreResponseDto getStoreById(Long id);

}
