package com.example.springplusteamproject.domain.order.service;


import com.example.springplusteamproject.domain.flower.entity.Flower;

public interface FlowerLockService {
    //비관적락
    Flower decreaseStock(Long id, int quantity);
    //비관적락 재시도 적용
    Flower decreaseRetryStock(Long id, int quantity);


}
