package com.example.springplusteamproject.domain.order.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.order.repository.FlowerPessiMisticLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlowerRedisson {

    private final RedissonClient redissonClient;
    private final FlowerRepository flowerRepository;
    private final FlowerPessiMisticLockRepository flowerPessiMisticLock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Flower decreaseStock(Long flowerId, int quantity) {
        String lockKey = "flower:" + flowerId + ":lock";
        RLock lock = redissonClient.getFairLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(500, -1, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                throw new RuntimeException("락 획득 실패: " + lockKey);
            }
                Flower flower = flowerRepository.findById(flowerId).orElseThrow(()->new ApiException(ErrorStatus.ORDER_FLOWER_NOTFOUND));
                log.info("재고 감소전 >>> {}",flower.getStock());
                flower.decreaseStock(quantity);
                log.info("재고 감소후 >>> {}",flower.getStock());
                return flowerRepository.saveAndFlush(flower);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("재고 - 락 인터럽트", e);
            throw new ApiException(ErrorStatus.ORDER_BAD_REQUEST);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

