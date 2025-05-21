package com.example.springplusteamproject.domain.order.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.order.dto.request.OrderRequestDTO;
import com.example.springplusteamproject.domain.order.dto.response.OrderResponseDTO;
import com.example.springplusteamproject.domain.order.service.OrderService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문하기",
            description = "주문을 요청합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    //주문하기
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
        @RequestBody @Valid OrderRequestDTO requestDTO,
        @AuthenticationPrincipal CustomUserPrincipal user){
         OrderResponseDTO responseDTO = orderService.createOrder(requestDTO,user.getId());
    return ApiResponse.onSuccess(SuccessStatus.ORDER_SUCCESS,responseDTO);
    }

}
