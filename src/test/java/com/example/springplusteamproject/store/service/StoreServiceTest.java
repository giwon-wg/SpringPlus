package com.example.springplusteamproject.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Pageable;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.springplusteamproject.common.config.RedissonConfig;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.request.CursorPageRequest;
import com.example.springplusteamproject.common.response.CursorPageResponse;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.store.dto.request.StoreCheckNameRequestDto;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.store.service.StoreServiceImpl;
import com.example.springplusteamproject.domain.store.service.StoreTransactionalService;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.domain.user.service.UserService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreTransactionalService storeTransactionalService;

    @Mock
    private RedisTemplate<String, Long> longRedisTemplate;

    private StoreServiceImpl storeService;

    private Store store;

    private User user;

    private final CustomUserPrincipal principal = new CustomUserPrincipal(
        User.builder()
            .id(1L)
            .email("test@example.com")
            .userRole(UserRole.CUSTOMER)
            .isDeleted(false)
            .build()
    );

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(storeRepository, redissonClient, storeTransactionalService, longRedisTemplate);

        user = createTestUser();
        store = createTestStore(user);
    }

    private User createTestUser() {
        return User.builder()
            .id(1L)
            .email("test@example.com")
            .userRole(UserRole.CUSTOMER)
            .nickname("테스트유저")
            .address("서울특별시")
            .brn("테스트")
            .image("이미지")
            .isDeleted(false)
            .build();
    }

    private Store createTestStore(User user) {
        return Store.builder()
            .id(1L)
            .name("안개꽃 화원")
            .address("대구 광역시")
            .image("이미지")
            .phoneNumber("010-1234-5678")
            .minOrderPrice(15000L)
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(18, 0))
            .deleted(false)
            .user(user)
            .build();
    }

    private CustomUserPrincipal createPrincipal(User user) {
        return new CustomUserPrincipal(user);
    }

    @Test
    void 가게_생성_락_획득실패_예외() throws InterruptedException {
        // given
        StoreRequestDto dto = new StoreRequestDto("중복", "서울", "...", "...", 10000L, "09:00", "18:00");

        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(anyLong(), anyLong(), any())).thenReturn(false); // 락 획득 실패

        // when & then
        ApiException ex = assertThrows(ApiException.class, () ->
            storeService.createStore(dto, principal)
        );

        assertEquals(ErrorStatus.STORE_BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void 가게_생성_락해제_예외_발생해도_정상종료() throws InterruptedException {
        // given
        StoreRequestDto dto = new StoreRequestDto("장미 카페", "서울", "img", "010", 10000L, "09:00", "18:00");
        CustomUserPrincipal principal = new CustomUserPrincipal(
            User.builder().id(1L).email("test@example.com").userRole(UserRole.CUSTOMER).isDeleted(false).build()
        );

        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(storeTransactionalService.saveStore(any(), any())).thenReturn(mock(StoreResponseDto.class));
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);
        doThrow(new RuntimeException("unlock error")).when(mockLock).unlock();

        // when
        StoreResponseDto response = storeService.createStore(dto, principal);

        // then
        assertNotNull(response);
    }

    @Test
    void 가게_생성_인터럽트_예외() throws InterruptedException {
        // given
        StoreRequestDto dto = new StoreRequestDto("중복", "서울", "...", "...", 10000L, "09:00", "18:00");

        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(anyLong(), anyLong(), any())).thenThrow(new InterruptedException());

        // when & then
        ApiException ex = assertThrows(ApiException.class, () ->
            storeService.createStore(dto, principal)
        );

        assertEquals(ErrorStatus.STORE_BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void 가게_삭제_성공() {
        // given
        when(storeRepository.findByUserIdAndDeletedFalse(user.getId()))
            .thenReturn(Optional.of(store));

        // when
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        storeService.deleteStore(principal);

        // then
        assertThat(store.getDeleted()).isTrue();
    }

    @Test
    void 가게_삭제_Id없음_예외() {

        when(storeRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Optional.empty());

        CustomUserPrincipal principal = new CustomUserPrincipal(user);

        assertThrows(ApiException.class, () -> storeService.deleteStore(principal));

    }

    @Test
    void 전체_가게_조회_성공() {

        // given
        List<Store> stores = List.of(
            Store.builder()
                .id(1L)
                .name("장미 화원")
                .address("대구 광역시")
                .image("이미지")
                .phoneNumber("010-1234-5678")
                .minOrderPrice(10000L)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(20, 0))
                .deleted(false)
                .user(user)
                .build(),
            Store.builder()
                .id(2L)
                .name("튤립 화원")
                .address("서을 특별시")
                .image("이미지")
                .phoneNumber("010-8765-4321")
                .minOrderPrice(12000L)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(21, 0))
                .deleted(false)
                .user(user)
                .build()
        );

        when(storeRepository.findByDeletedFalse()).thenReturn(stores);

        // when
        List<StoreListResponseDto> response = storeService.getAllStores();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getName()).isEqualTo("장미 화원");
        assertThat(response.get(1).getName()).isEqualTo("튤립 화원");
    }

    @Test
    void 커서기반_전체조회_성공() {
        // given
        Long cursor = 100L;
        int size = 2;

        Store store1 = store.builder()
            .id(99L)
            .name("장미 화원")
            .deleted(false)
            .build();

        Store store2 = store.builder()
            .id(98L)
            .name("튤립 화원")
            .deleted(false)
            .build();

        List<Store> mockResult = List.of(store1, store2);
        Pageable pageable = PageRequest.of(0, size);

        CursorPageRequest request = new CursorPageRequest(cursor, size);

        given(storeRepository.findByCursor(cursor, pageable)).willReturn(mockResult);

        // when
        CursorPageResponse<StoreListResponseDto> result = storeService.getStoresByCursor(request);

        // then
        assertThat(result.items()).hasSize(2);
        assertThat(result.items().get(0).getName()).isEqualTo("장미 화원");
        assertThat(result.items().get(1).getName()).isEqualTo("튤립 화원");

        assertThat(result.pageInfo().nextCursor()).isEqualTo(98L);
        assertThat(result.pageInfo().hasNext()).isFalse();

        verify(storeRepository).findByCursor(cursor, pageable);
    }

    @Test
    void 단일_가게_조회_성공() {
        // given
        Store store = Store.builder()
            .id(1L)
            .name("장미 화원")
            .address("제주 특별시")
            .image("이미지")
            .phoneNumber("010-1234-5678")
            .minOrderPrice(11000L)
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(18, 0))
            .deleted(false)
            .user(user)
            .build();

        when(storeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(store));

        // when
        StoreResponseDto response = storeService.getStoreById(1L);

        // then
        assertThat(response.getName()).isEqualTo("장미 화원");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getImage()).isEqualTo("이미지");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getMinOrderPrice()).isEqualTo(11000L);
        assertThat(response.getOpenTime()).isEqualTo("09:00");
        assertThat(response.getCloseTime()).isEqualTo("18:00");
    }

    @Test
    void 단일_가게_조회_Id없음_예외() {
        // given
        when(storeRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ApiException.class, () -> storeService.getStoreById(999L));
    }

    @Test
    void 상호명_사용가능() {
        // given
        String name = "튤립화원";
        StoreCheckNameRequestDto dto = new StoreCheckNameRequestDto(name);
        when(storeRepository.existsByNameAndDeletedFalse(name)).thenReturn(false);

        // when
        String result = storeService.checkingName(dto);

        // then
        assertThat(result).isEqualTo("사용 가능한 상호명입니다.");
    }

    @Test
    void 상호명_중복() {
        // given
        String name = "장미화원";
        StoreCheckNameRequestDto dto = new StoreCheckNameRequestDto(name);
        when(storeRepository.existsByNameAndDeletedFalse(name)).thenReturn(true);

        // when
        String result = storeService.checkingName(dto);

        // then
        assertThat(result).isEqualTo("이미 존재하는 상호명입니다.");
    }

    @Test
    void 상호명_금칙어필터() {
        // given
        String name = "운영자화원";
        StoreCheckNameRequestDto dto = new StoreCheckNameRequestDto(name);
        when(storeRepository.existsByNameAndDeletedFalse(name)).thenReturn(false);

        // when
        String result = storeService.checkingName(dto);

        // then
        assertThat(result).isEqualTo("부적절한 단어가 포함되어 있습니다.");
    }
}
