package com.example.springplusteamproject.domain.flower.controller;

import static com.example.springplusteamproject.common.status.SuccessStatus.CUSTOM_SUCCESS_STATUS;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.service.FlowerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flower", description = "꽃 API")
@RestController
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @PostMapping("/owner/stores/{storeId}/flowers")
    public ResponseEntity<ApiResponse<FlowerResponseDto.Create>> createFlower(
        @PathVariable Long storeId,
        @Valid @RequestBody FlowerRequestDto.Create request
    ) {
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, flowerService.createFlower(storeId, request));
    }

    @PatchMapping("/owner/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<Void>> updateFlower(
        @PathVariable Long storeId,
        @PathVariable Long flowerId,
        @Valid @RequestBody FlowerRequestDto.Update request
    ) {
        flowerService.updateFlower(storeId, flowerId, request);
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, null);
    }

    @GetMapping("/owner/stores/{storeId}/flowers")
    public ResponseEntity<ApiResponse<Page<FlowerResponseDto.Get>>> getMyFlowers(
        @PathVariable Long storeId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, flowerService.getMyFlowers(storeId, page, size));
    }

    @GetMapping("/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<FlowerResponseDto.Get>> getFlowerDetail(
        @PathVariable Long storeId,
        @PathVariable Long flowerId
    ) {
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, flowerService.getFlowerDetails(storeId, flowerId));
    }

    @DeleteMapping("/owner/stores/{storeId}/flowers/{flowerId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlower(
        @PathVariable Long storeId,
        @PathVariable Long flowerId
    ) {
        flowerService.deleteFlower(storeId, flowerId);
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, null);
    }

}
