package com.example.springplusteamproject.domain.flower.repository;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowerRepository extends JpaRepository<Flower, Long> {


}
