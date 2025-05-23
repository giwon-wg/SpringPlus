package com.example.springplusteamproject.domain.store.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PopularStoreServiceImpl implements PopularStoreService {

    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public List<StoreResponseDto> getPopularStoresByView() {
        Set<String> keys = redisTemplate.keys("store:viewcount:*");

        return keys.stream()
            .map(k -> {
                Long id = Long.parseLong(k.replace("store:viewcount:", ""));
                Long count = redisTemplate.opsForValue().get(k);
                return Map.entry(id, count);
            })
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(10)
            .map(e -> storeRepository.findByIdAndDeletedFalse(e.getKey()).orElse(null))
            .filter(Objects::nonNull)
            .map(StoreResponseDto::fromEntity)
            .toList();
    }

}
