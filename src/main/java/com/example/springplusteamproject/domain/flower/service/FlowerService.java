package com.example.springplusteamproject.domain.flower.service;

import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;


public interface FlowerService {

    // 상품 등록
    FlowerResponseDto.Create createFlower(Long storeId, FlowerRequestDto.Create request, Long userId);

    // 상품 수정
    void updateFlower(Long storeId, Long flowerId, FlowerRequestDto.Update request, Long userId);

    // 상품 조회 (본인 가게의 상품만)
    Page<FlowerResponseDto.Get> getMyFlowers(Long storeId, int page, int size);

    // 상품 상세 조회
    FlowerResponseDto.Get getFlowerDetails(Long storeId, Long flowerId);

    // 상품 삭제
    void deleteFlower(Long storeId, Long flowerId, Long userId);

    // 상품 검색
    Page<FlowerResponseDto.Get> searchFlowers(String keyword, Long userId, int page, int size);

    // 인기 검색어 조회
    List<FlowerSearchResponseDto> getTop10Keywords(Integer year, Integer month, Integer day);

}
