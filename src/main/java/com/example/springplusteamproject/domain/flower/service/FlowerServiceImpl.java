package com.example.springplusteamproject.domain.flower.service;

import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto.Update;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Create;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlowerServiceImpl implements FlowerService {

    private final StoreRepository storeRepository;
    private final FlowerRepository flowerRepository;

    @Override
    @Transactional
    public Create createFlower(Long storeId, FlowerRequestDto.Create request) {

        // 가게 조회
        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new IllegalArgumentException("id가 없는데요?"));

        // 본인 가게에서만 상품 생성 가능
        if (store.getUser().getId() != 1L) {
            throw new RuntimeException();
        }

        // 상품 생성
        Flower flower = Flower.from(store, request);
        flowerRepository.save(flower);

        return new FlowerResponseDto.Create(flower.getId(), flower.getName());
    }

    @Override
    public Void updateFlower(Long storeId, Long flowerId, Update request) {
        return null;
    }

    @Override
    public List<Get> getMyFlowers(Long storeId) {
        return List.of();
    }

    @Override
    public Get getFlowerDetail(Long storeId, Long flowerId) {
        return null;
    }

    @Override
    public Void deleteFlower(Long storeId, Long flowerId) {
        return null;
    }
}
