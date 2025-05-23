package com.example.springplusteamproject.domain.order.repository;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlowerPessiMisticLockRepository extends JpaRepository<Flower,Long> {


    //비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name= " jakarta.persistence.lock.timeout", value = "3000")})// 3초 대기
    @Query("select f from Flower f where f.id = :id")
    Optional<Flower> findByIdForUpdate(Long id);

}
