package com.example.springplusteamproject.domain.flower.controller;

import static com.example.springplusteamproject.common.status.SuccessStatus.FLOWER_CREATE_SUCCESS;
import static com.example.springplusteamproject.common.status.SuccessStatus.FLOWER_OPERATION_SUCCESS;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import com.example.springplusteamproject.domain.flower.enums.SearchType;
import com.example.springplusteamproject.domain.flower.service.FlowerService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flower", description = "꽃 상품 API")
@RestController
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @Operation(
        summary = "꽃 상품 생성",
        description = "사장님은 새로운 꽃 상품을 등록할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PostMapping("/owner/stores/{storeId}/flowers")
    public ResponseEntity<ApiResponse<FlowerResponseDto.Create>> createFlower(
        @PathVariable Long storeId,
        @Valid @RequestBody FlowerRequestDto.Create request,
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.onSuccess(FLOWER_CREATE_SUCCESS,
            flowerService.createFlower(storeId, request, principal.getId()));
    }

    @Operation(
        summary = "꽃 상품 수정",
        description = "사장님은 선택한 꽃 상품의 상세 정보를 수정할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PatchMapping("/owner/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<Void>> updateFlower(
        @PathVariable Long storeId,
        @PathVariable Long flowerId,
        @Valid @RequestBody FlowerRequestDto.Update request,
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        flowerService.updateFlower(storeId, flowerId, request, principal.getId());
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS, null);
    }

    @Operation(
        summary = "본인 가게의 꽃 상품 목록 조회",
        description = "사장님은 본인 가게의 꽃 상품 목록을 조회할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/owner/stores/{storeId}/flowers")
    public ResponseEntity<ApiResponse<Page<FlowerResponseDto.Get>>> getMyFlowers(
        @PathVariable Long storeId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS,
            flowerService.getMyFlowers(storeId, page, size));
    }

    @Operation(
        summary = "꽃 상품 상세 조회",
        description = "손님은 선택한 꽃 상품의 상세 정보를 조회할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<FlowerResponseDto.Get>> getFlowerDetail(
        @PathVariable Long storeId,
        @PathVariable Long flowerId
    ) {
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS,
            flowerService.getFlowerDetails(storeId, flowerId));
    }

    @Operation(
        summary = "꽃 상품 삭제",
        description = "사장님은 선택한 꽃 상품을 삭제할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @DeleteMapping("/owner/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlower(
        @PathVariable Long storeId,
        @PathVariable Long flowerId,
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        flowerService.deleteFlower(storeId, flowerId, principal.getId());
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS, null);
    }

    @Operation(
        summary = "꽃 상품 검색",
        description = "꽃 이름을 기준으로 검색할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/flowers/search")
    public ResponseEntity<ApiResponse<Page<FlowerResponseDto.Get>>> searchFlowers(
        @RequestParam String keyword,
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS,
            flowerService.searchFlowers(keyword, principal.getId(), page, size));
    }

    @Operation(
        summary = "꽃 상품 검색 V2",
        description = "인기 검색 top10 캐싱이 적용된 상품 검색 기능을 이용할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/flowers/search/v2")
    public ResponseEntity<ApiResponse<Page<FlowerResponseDto.Get>>> searchFlowersV2(
        @RequestParam String keyword,
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS,
            flowerService.searchFlowersV2(keyword, principal.getId(), page, size));
    }

    @Operation(
        summary = "꽃 상품 인기 검색어 조회",
        description = "일간/월간/연간 인기 검색어를 조회할 수 있습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/flowers/search/top10")
    public ResponseEntity<ApiResponse<List<FlowerSearchResponseDto>>> getTop10Keywords(
        @RequestParam SearchType type
    ) {
        return ApiResponse.onSuccess(FLOWER_OPERATION_SUCCESS,
            flowerService.getTop10Keywords(type));
    }
}
