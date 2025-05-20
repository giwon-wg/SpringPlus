package com.example.springplusteamproject.domain.store.entity;

import java.time.LocalTime;

import com.example.springplusteamproject.common.entity.BaseEntity;
import com.example.springplusteamproject.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "store",
    indexes = {
        @Index(name = "idx_store_name_deleted", columnList = "store_name, deleted"),
        @Index(name = "idx_user_id_deleted", columnList = "user_id, deleted")
    }
)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "store_name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String image;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Long minOrderPrice;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private Boolean deleted = false;

    public void setDeleted() {
        this.deleted = true;
    }

}
