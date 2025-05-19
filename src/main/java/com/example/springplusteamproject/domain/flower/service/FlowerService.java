package com.example.springplusteamproject.domain.flower.service;

import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import java.util.List;


public interface FlowerService {

    // 상품 등록
    FlowerResponseDto.Create createFlower(Long storeId, FlowerRequestDto.Create request);

    // 상품 수정
    Void updateFlower(Long storeId, Long flowerId, FlowerRequestDto.Update request);

    // 상품 조회 (본인 가게의 상품만)
    List<FlowerResponseDto.Get> getMyFlowers(Long storeId);

    // 상품 검색
//    List<FlowerResponseDto.Get> searchFlowers();

    // 상품 상세 조회
    FlowerResponseDto.Get getFlowerDetail(Long storeId, Long flowerId);

    // 상품 삭제
    Void deleteFlower(Long storeId, Long flowerId);

}
