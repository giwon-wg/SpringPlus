package com.example.springplusteamproject.domain.flower.controller;

import static com.example.springplusteamproject.common.status.SuccessStatus.CUSTOM_SUCCESS_STATUS;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.service.FlowerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flower", description = "꽃 API")
@RestController
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @RequestMapping("/owner/stores/{storeId}/flowers")
    public ResponseEntity<ApiResponse<FlowerResponseDto.Create>> createFlower(
        @PathVariable Long storeId,
        @Valid @RequestBody FlowerRequestDto.Create request
    ) {
        FlowerResponseDto.Create response = flowerService.createFlower(storeId, request);
        return ApiResponse.onSuccess(CUSTOM_SUCCESS_STATUS, response);
    }
}
