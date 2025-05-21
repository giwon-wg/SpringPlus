package com.example.springplusteamproject.domain.order.service;


import com.example.springplusteamproject.domain.order.dto.request.OrderRequestDTO;
import com.example.springplusteamproject.domain.order.dto.response.OrderResponseDTO;
import com.example.springplusteamproject.security.CustomUserPrincipal;

public interface OrderService {

    //주문하기
    OrderResponseDTO createOrder(OrderRequestDTO requestDTO, CustomUserPrincipal principal);
}
