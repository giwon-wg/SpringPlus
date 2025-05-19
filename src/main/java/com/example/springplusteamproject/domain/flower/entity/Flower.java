package com.example.springplusteamproject.domain.flower.entity;

import com.example.springplusteamproject.common.entity.BaseEntity;
import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import com.example.springplusteamproject.domain.store.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "flower")
public class Flower extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private Color color;

    @Column(nullable = false)
    private Season season;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private Boolean deleted = false;

    public void setDeleted() {
        this.deleted = true;
    }

}

