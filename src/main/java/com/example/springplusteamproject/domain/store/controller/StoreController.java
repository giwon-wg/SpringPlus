package com.example.springplusteamproject.domain.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springplusteamproject.common.request.CursorPageRequest;
import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.response.CursorPageResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.store.dto.request.StoreCheckNameRequestDto;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.service.PopularStoreService;
import com.example.springplusteamproject.domain.store.service.StoreService;
import com.example.springplusteamproject.security.CustomUserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Store", description = "가게 API")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final PopularStoreService popularStoreService;

    @Operation(
        summary = "가게 생성",
        description = "가게를 생성합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping("/owner/stores")
    public ResponseEntity<ApiResponse<StoreResponseDto>> createStore(
        @Valid @RequestBody StoreRequestDto dto,
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        StoreResponseDto responseDto = storeService.createStore(dto, principal);
        return ApiResponse.onSuccess(SuccessStatus.STORE_CREATED_SUCCESS, responseDto);
    }

    @Operation(
        summary = "가게 폐업",
        description = "가게를 폐업합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @DeleteMapping("/owner/stores")
    public ResponseEntity<ApiResponse<StoreResponseDto>> deleteStore(
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        storeService.deleteStore(principal);
        return ApiResponse.onSuccess(SuccessStatus.STORE_SUCCESS, null);
    }

    @Operation(
        summary = "커서 기반 가게 조회",
        description = "커서 기반으로 모든 가게를 조회합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/stores")
    public ResponseEntity<ApiResponse<CursorPageResponse<StoreListResponseDto>>> findStoreByCursor(
        @Valid @ModelAttribute CursorPageRequest request
    ) {
        return ApiResponse.onSuccess(SuccessStatus.STORE_SUCCESS, storeService.getStoresByCursor(request));
    }

    @Operation(
        summary = "가게 단건 조회",
        description = "id를 기반으로 가게를 조회합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStoreById(
        @PathVariable Long storeId
    ) {
        return ApiResponse.onSuccess(SuccessStatus.STORE_SUCCESS, storeService.getStoreById(storeId));
    }

    @Operation(
        summary = "상표명 사용 가능 여부 확인",
        description = "상표명의 중복과 사용금지 단어 유무를 확인합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping("/owner/stores/checkName")
    public ResponseEntity<ApiResponse<String>> checkingName(
        @Valid @RequestBody StoreCheckNameRequestDto dto
    ) {
        String message = storeService.checkingName(dto);
        return ApiResponse.onSuccess(SuccessStatus.STORE_SUCCESS, message);
    }

    @Operation(
        summary = "인기 순위 조회",
        description = "인기 순위 top10을 조회 합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/stores/popular")
    public ResponseEntity<ApiResponse<List<StoreResponseDto>>> getPopularStores() {
        return ApiResponse.onSuccess(SuccessStatus.STORE_SUCCESS, popularStoreService.getPopularStoresFromCache());
    }
}
