package com.example.springplusteamproject.domain.order.entity;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.order.vo.Price;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="order_id",nullable = false)
    private Order order;

    //꽃 (상품)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id", nullable = false)
    private Flower flower;

    //수량
    private int quantity;

    @Embedded
    private Price price;

    //양방향 편의메서드
    public void setOrder(Order order) {
        this.order = order;
    }

    OrderItem(Order order, Flower flower,int quantity ,Price price){
        this.order = order;
        this.flower = flower;
        this.quantity = quantity;
        this.price = price;
    }
    public static OrderItem of(Order order, Flower flower, int quantity, Price price) {
        return new OrderItem(order, flower, quantity, price);
    }
}
