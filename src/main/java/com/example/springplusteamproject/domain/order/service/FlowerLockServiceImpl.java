
package com.example.springplusteamproject.domain.order.service;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.order.repository.FlowerPessiMisticLockRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowerLockServiceImpl implements FlowerLockService {

    private final FlowerPessiMisticLockRepository flowerPessiMisticLock;

    //비관락
    @Override
    public Flower decreaseStock(Long id, int quantity) {
            Flower flower = flowerPessiMisticLock.findByIdForUpdate(id).orElseThrow();
            flower.decreaseStock(quantity);
            log.info(flower.getStock().toString());
            return flowerPessiMisticLock.saveAndFlush(flower);
        }

    //비관락 재시도
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Flower decreaseRetryStock(Long id, int quantity) {
        int maxRetries = 3; //3번 재시도
        for (int i = 0; i < maxRetries; i++) {

            try {
                Flower foundFlower = flowerPessiMisticLock.findByIdForUpdate(id)
                    .orElseThrow(() -> new ApiException(ErrorStatus.ORDER_FLOWER_NOTFOUND));
                foundFlower.decreaseStock(quantity);

                return foundFlower;
            } catch (PessimisticLockException | LockTimeoutException | LockAcquisitionException e) {
                log.warn("Deadlock/LockTimeout 발생: {}회차, 예외 타입: {}", i + 1, e.getClass().getSimpleName());

                if (i == maxRetries - 1) {
                    log.error("재고 차감 중 데드락/락타임아웃 (최대 재시도 초과)", e);
                    throw new IllegalStateException("에러");

                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        }
        throw new ApiException(ErrorStatus.ORDER_FLOWER_NOTFOUND);
        }
}

