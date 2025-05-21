package com.example.springplusteamproject.domain.store.service;

import java.util.List;

import com.example.springplusteamproject.common.request.CursorPageRequest;
import com.example.springplusteamproject.common.response.CursorPageResponse;
import com.example.springplusteamproject.domain.store.dto.request.StoreCheckNameRequestDto;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.security.CustomUserPrincipal;

public interface StoreService{

    StoreResponseDto createStore(StoreRequestDto dto, CustomUserPrincipal principal);

    void deleteStore(CustomUserPrincipal principal);

    List<StoreListResponseDto> getAllStores();

    StoreResponseDto getStoreById(Long id);

    String checkingName(StoreCheckNameRequestDto dto);

    CursorPageResponse<StoreListResponseDto> getStoresByCursor(CursorPageRequest cursorPageRequest);

}
