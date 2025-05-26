package com.example.springplusteamproject.domain.flower.init;

import static com.example.springplusteamproject.common.status.ErrorStatus.STORE_NOT_FOUND;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlowerDummyDataLoader {

    private final FlowerRepository flowerRepository;
    private final StoreRepository storeRepository;

    @PostConstruct
    public void initDummyData() {
        Store store = storeRepository.findById(4L)
            .orElseThrow(() -> new ApiException(STORE_NOT_FOUND));

        Random random = new Random();
        List<Flower> flowers = new ArrayList<>();

        for (int i = 1; i <= 50000; i++) {
            Flower flower = Flower.builder()
                .store(store)
                .name("테스트꽃" + i)
                .description("설명 " + i)
                .type(Type.values()[random.nextInt(Type.values().length)])
                .color(Color.values()[random.nextInt(Color.values().length)])
                .season(Season.values()[random.nextInt(Season.values().length)])
                .price((random.nextInt(100) + 10) * 100)
                .stock(random.nextInt(100) + 1)
                .expirationDate(LocalDate.now().plusDays(random.nextInt(30) + 1))
                .deleted(false)
                .build();

            flowers.add(flower);

            // 1000개씩 벌크 저장
            if (i % 1000 == 0) {
                flowerRepository.saveAll(flowers);
                flowers.clear();
            }
        }

        // 남은 데이터 저장
        if (!flowers.isEmpty()) {
            flowerRepository.saveAll(flowers);
        }

        System.out.println("성공");
    }
}
