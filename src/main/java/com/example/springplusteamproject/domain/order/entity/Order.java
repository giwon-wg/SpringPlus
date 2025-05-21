package com.example.springplusteamproject.domain.order.entity;

import com.example.springplusteamproject.common.entity.BaseEntity;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.order.vo.Price;
import com.example.springplusteamproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name ="orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    쿠폰  nullable
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;  //주문 생성시 요청

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private Price price;

    //양방향 편의 메서드
    public void addOrderItem(OrderItem item){
        if(!this.orderItems.contains(item)){
        this.orderItems.add(item);
        item.setOrder(this);
        }
    }

    @Builder
    public Order(User user, OrderStatus orderStatus, Price price) {
        this.user = user;
        this.orderStatus = orderStatus;
        this.price = price;
        this.orderItems = new ArrayList<>();
    }

    public static Order of(User user,Price price,List<OrderItem> orderItems){
        Order order = Order.builder()
            .user(user)
            .orderStatus(OrderStatus.PENDING)
            .price(price)
            .build();

        for(OrderItem item : orderItems) {
            order.addOrderItem(item);
        }
        return order;
    }
}
