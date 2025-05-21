package com.example.springplusteamproject.domain.flower.entity;

import com.example.springplusteamproject.common.entity.BaseEntity;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.enums.Color;
import com.example.springplusteamproject.domain.flower.enums.Season;
import com.example.springplusteamproject.domain.flower.enums.Type;
import com.example.springplusteamproject.domain.store.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Season season;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Builder.Default
    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean deleted = false;

    public static Flower from(Store store, FlowerRequestDto.Create dto) {
        return Flower.builder()
            .store(store)
            .name(dto.getName())
            .description(dto.getDescription())
            .type(dto.getType())
            .color(dto.getColor())
            .season(dto.getSeason())
            .price(dto.getPrice())
            .stock(dto.getStock())
            .expirationDate(LocalDate.parse(dto.getExpirationDate()))
            .build();
    }

    public void setDeleted() {
        this.deleted = true;
    }

    public void update(FlowerRequestDto.Update dto) {

        if (dto.getName() != null) {
            this.name = dto.getName();
        }

        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }

        if (dto.getType() != null) {
            this.type = dto.getType();
        }

        if (dto.getColor() != null) {
            this.color = dto.getColor();
        }

        if (dto.getSeason() != null) {
            this.season = dto.getSeason();
        }

        if (dto.getPrice() != null) {
            this.price = dto.getPrice();
        }

        if (dto.getStock() != null) {
            this.stock = dto.getStock();
        }
    }

}

