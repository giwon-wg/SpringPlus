package com.example.springplusteamproject.domain.order.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.order.dto.request.OrderItemRequestDTO;
import com.example.springplusteamproject.domain.order.dto.request.OrderRequestDTO;
import com.example.springplusteamproject.domain.order.dto.response.OrderResponseDTO;
import com.example.springplusteamproject.domain.order.entity.Order;
import com.example.springplusteamproject.domain.order.entity.OrderItem;
import com.example.springplusteamproject.domain.order.repository.FlowerPessiMisticLockRepository;
import com.example.springplusteamproject.domain.order.repository.OrderRepository;
import com.example.springplusteamproject.domain.order.vo.Price;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final FlowerPessiMisticLockRepository flowerPessiMisticLockRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final FlowerLockService flowerPessiMisticLockService;
    private final FlowerRedisson flowerRedisson;

    //주문하기
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO, CustomUserPrincipal principal) {

        //유저 조회
        User user = userRepository.findById(principal.getId()).orElseThrow(() -> new ApiException(ErrorStatus.ORDER_USER_NOT_FOUND));

        List<OrderItem> orderItems = new ArrayList<>();
        //전체 금액
        int priceTotal = 0;
        Long discount = 0L;
        for(OrderItemRequestDTO item : requestDTO.getItems()) {
            // 꽃 Id(상품Id)
            Long flowerId = item.getFlowerId();
            //수량
            int quantity = item.getQuantity();

            /*재고 감소 */
            /* 비관락 적용*/
//            Flower foundFlower = flowerPessiMisticLockService.decreaseStock(flowerId, quantity);
            /* 비관락 재시도 적용*/
//            Flower foundFlower = flowerPessiMisticLockService.decreaseRetryStock(flowerId, quantity);

            // 분산락
//            Flower foundFlower =flowerRedisson.decreaseStock(flowerId,quantity);
            Flower foundFlower =flowerRedisson.decreaseStock(flowerId,quantity);


            //단가
            int unitPrice = foundFlower.getPrice();
            int subTotal = unitPrice * quantity;
            priceTotal += subTotal;

            Price price = Price.of(subTotal, discount);
            OrderItem orderItem = OrderItem.of(null, foundFlower, quantity, price);
            orderItems.add(orderItem);
        }
        UserCoupon foundUserCoupon = null;

        //쿠폰이있으면
        if(requestDTO.getUserCouponId() != null) {
                foundUserCoupon = userCouponRepository.findById(requestDTO.getUserCouponId())
                .orElseThrow(() -> new ApiException(ErrorStatus.ORDER_COUPON_NOTFOUND));

            //본인확인
            if(!foundUserCoupon.getUser().getId().equals(user.getId())){
                throw new ApiException(ErrorStatus.ORDER_COUPON_OWNER_MISMATCH);
            }

            //중복 방지
            if(foundUserCoupon.isUsed()){
                throw new ApiException(ErrorStatus.ORDER_COUPON_ALREADY_USED);
            }

            //할인
            discount = foundUserCoupon.getDiscountCoupon().getDiscount();
            //dirty
            foundUserCoupon.useCoupon();


        }
        //가격 저장
        Price price = Price.of(priceTotal,discount);

        //주문 저장
        Order order = Order.of(user, price, orderItems);

        if(foundUserCoupon != null){
            order.setUserCoupon(foundUserCoupon);
        }
        Order savedOrder = orderRepository.save(order);

        //결제 API

        //dirty PAID
        order.updateStatus();
        return OrderResponseDTO.from(savedOrder);
    }

}
