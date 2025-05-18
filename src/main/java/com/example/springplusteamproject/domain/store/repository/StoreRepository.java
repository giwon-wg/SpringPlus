package com.example.springplusteamproject.domain.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    //소프트 삭제 고려한 중복 검사
    boolean existsByNameAndDeletedFalse(String name);

    // 소프트 삭제 고려한 이름으로 조회
    Optional<Store> findByNameAndDeletedFalse(String name);

    // 소프트 삭제 고려한 ID로 조회
    Optional<Store> findByIdAndDeletedFalse(Long id);

    List<StoreResponseDto> findAllAndDeletedFalse(Boolean deleted);
}
