package com.example.springplusteamproject.domain.store.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularStoreServiceImpl implements PopularStoreService {

    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Override
    public List<StoreResponseDto> getPopularStoresByView() {
        Set<String> keys = longRedisTemplate.keys("store:viewcount:*");

        return keys.stream()
            .map(k -> {
                Long id = Long.parseLong(k.replace("store:viewcount:", ""));
                Object raw = longRedisTemplate.opsForValue().get(k);
                Long count = (raw instanceof Long) ? (Long) raw
                    : (raw instanceof Integer) ? ((Integer) raw).longValue()
                    : 0L;
                return Map.entry(id, count);
            })
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(10)
            .map(e -> storeRepository.findByIdAndDeletedFalse(e.getKey()).orElse(null))
            .filter(Objects::nonNull)
            .map(StoreResponseDto::fromEntity)
            .toList();
    }

    @Override
    public List<StoreResponseDto> getPopularStoresFromCache() {
        List<StoreResponseDto> cached = (List<StoreResponseDto>) objectRedisTemplate.opsForValue().get("popular:stores:view");
        if (cached != null) {
            log.info("[Store - 캐싱]인기 상점 캐시에서 조회됨");
            return cached;
        }
        log.warn("[Store - 캐싱]캐시 미존재, 실시간 계산으로 대체");
        return getPopularStoresByView();
    }

    @Scheduled(initialDelay = 0, fixedRate = 600_000)
    public void updatePopularStoresCache() {
        List<StoreResponseDto> topStores = getPopularStoresByView();
        objectRedisTemplate.opsForValue().set("popular:stores:view", topStores, CACHE_TTL);
        log.info("[Store - 캐싱]인기 상점 캐시 갱신 완료 ({}개)", topStores.size());
    }
}
