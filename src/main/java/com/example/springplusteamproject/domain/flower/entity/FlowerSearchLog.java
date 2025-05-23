package com.example.springplusteamproject.domain.flower.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "flowerSearchLog")
public class FlowerSearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    @Column(nullable = false, updatable = false)
    private int year;

    @Column(nullable = false, updatable = false)
    private int month;

    @Column(nullable = false, updatable = false)
    private int day;

    public FlowerSearchLog(Long userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchedAt = LocalDateTime.now();
        this.year = searchedAt.getYear();
        this.month = searchedAt.getMonthValue();
        this.day = searchedAt.getDayOfMonth();
    }
}
