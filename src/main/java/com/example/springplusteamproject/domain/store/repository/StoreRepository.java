package com.example.springplusteamproject.domain.store.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springplusteamproject.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    //소프트 삭제 고려한 중복 검사
    boolean existsByNameAndDeletedFalse(String name);

    // 유저 한명당 1인 1가게 제한
    boolean existsByUserIdAndDeletedFalse(Long userId);

    // 소프트 삭제 고려한 이름으로 조회
    Optional<Store> findByNameAndDeletedFalse(String name);

    // 소프트 삭제 고려한 ID로 조회
    Optional<Store> findByIdAndDeletedFalse(Long id);

    // 소프트 삭제를 고려한 유저 ID로 조회
    Optional<Store> findByUserIdAndDeletedFalse(Long userId);

    // 커서기반 페이징 전체 조회
    @Query("SELECT s FROM Store s WHERE s.deleted = false "
        + "AND (:cursor IS NULL OR s.id < :cursor) "
        + "ORDER BY s.id DESC")
    List<Store> findByCursor(Long cursor, int size);

    List<Store> findByDeletedFalse();
}
