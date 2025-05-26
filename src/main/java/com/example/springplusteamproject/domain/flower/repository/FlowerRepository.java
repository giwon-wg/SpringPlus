package com.example.springplusteamproject.domain.flower.repository;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowerRepository extends JpaRepository<Flower, Long> {

    Optional<Flower> findByIdAndDeletedFalse(Long id);

    Page<Flower> findByStore_IdAndDeletedFalse(Long storeId, Pageable pageable);

    Page<Flower> findByNameContainingAndDeletedFalse(String keyword, Pageable pageable);

}
