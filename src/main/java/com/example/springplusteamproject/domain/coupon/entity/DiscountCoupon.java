package com.example.springplusteamproject.domain.coupon.entity;

import com.example.springplusteamproject.common.entity.BaseEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "discount_coupon")
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false)
    private Long discount;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private boolean isDeleted;

    public static DiscountCoupon of(Store store, String couponName, Long discount, LocalDateTime issuedAt,
                                     LocalDateTime expiresAt, Long quantity, Long stock) {
        return DiscountCoupon.builder()
            .store(store)
            .couponName(couponName)
            .discount(discount)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .quantity(quantity)
            .stock(stock)
            .isDeleted(false)
            .build();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
