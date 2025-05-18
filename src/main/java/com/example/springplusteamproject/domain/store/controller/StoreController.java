package com.example.springplusteamproject.domain.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.service.StoreService;
import com.example.springplusteamproject.domain.store.status.success.StoreSuccessCode;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Store", description = "가게 API")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/owner/stores")
    public ResponseEntity<ApiResponse<StoreResponseDto>> createStore(
        @Valid @RequestBody StoreRequestDto dto
    ) {
        StoreResponseDto responseDto = storeService.createStore(dto);
        return ApiResponse.onSuccess(StoreSuccessCode.STORE_ADD_SUCCESS, responseDto);
    }

}
