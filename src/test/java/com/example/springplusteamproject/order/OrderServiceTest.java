package com.example.springplusteamproject.order;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.order.dto.request.OrderItemRequestDTO;
import com.example.springplusteamproject.domain.order.dto.request.OrderRequestDTO;
import com.example.springplusteamproject.domain.order.dto.response.OrderResponseDTO;
import com.example.springplusteamproject.domain.order.entity.Order;
import com.example.springplusteamproject.domain.order.repository.OrderRepository;
import com.example.springplusteamproject.domain.order.service.FlowerLockService;
import com.example.springplusteamproject.domain.order.service.FlowerRedisson;
import com.example.springplusteamproject.domain.order.service.OrderServiceImpl;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.springplusteamproject.domain.flower.enums.Color.PINK;
import static com.example.springplusteamproject.domain.flower.enums.Season.ALL;
import static com.example.springplusteamproject.domain.flower.enums.Type.ROSE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FlowerLockService flowerPessiMisticLockService; // 비관락
    @Mock
    private FlowerRedisson flowerRedisson; // Redisson락
    @InjectMocks
    private OrderServiceImpl orderService;

    User user;
    Flower flower;
    UserCoupon userCoupon;
    DiscountCoupon discountCoupon;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .address("서울특별시")
                .brn("테스트")
                .email("email@test.com")
                .image("이미지")
                .nickname("다람지")
                .build();

        flower = Flower.builder()
                .id(1L)
                .name("자나장미")
                .description("영원한 사랑, 끝없는 사랑, 행복한 사랑")
                .type(ROSE)
                .color(PINK)
                .season(ALL)
                .price(700)
                .stock(100)
                .expirationDate(LocalDate.parse("2025-05-19"))
                .build();

        discountCoupon = DiscountCoupon.builder()
                .id(1L)
                .couponName("5000원 할인")
                .discount(5000L)
                .stock(500L)
                .build();

        userCoupon = UserCoupon.builder()
                .id(1L)
                .user(user)
                .discountCoupon(discountCoupon)
                .isUsed(false)
                .build();
    }

    @Transactional
    @Test
    void 주문_성공_재고차감_쿠폰사용됨() {
        // given
        OrderItemRequestDTO item = OrderItemRequestDTO.builder()
                .flowerId(flower.getId())
                .quantity(2)
                .build();
        OrderRequestDTO dto = OrderRequestDTO.builder()
                .items(List.of(item))
                .userCouponId(userCoupon.getId())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userCouponRepository.findById(userCoupon.getId())).thenReturn(Optional.of(userCoupon));

        // flowerRedisson.decreaseStock 호출 시 실제로 재고 차감된 flower 객체 리턴하도록 설정
        flower.decreaseStock(2);
        when(flowerRedisson.decreaseStock(flower.getId(), 2)).thenReturn(flower);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CustomUserPrincipal principal = new CustomUserPrincipal(user);


        // 주문 전 값 출력
        System.out.println("주문 전 - flower 재고: " + flower.getStock());
        System.out.println("주문 전 - userCoupon isUsed: " + userCoupon.isUsed());

        // when
        OrderResponseDTO result = orderService.createOrder(dto, principal);

        // 주문 후 값 출력
        System.out.println("주문 후 - flower 재고: " + flower.getStock());
        System.out.println("주문 후 - userCoupon isUsed: " + userCoupon.isUsed());

        // then
        assertThat(flower.getStock()).isEqualTo(98);   // 100-2
        assertThat(userCoupon.isUsed()).isTrue();
        assertThat(result).isNotNull();
    }


}



