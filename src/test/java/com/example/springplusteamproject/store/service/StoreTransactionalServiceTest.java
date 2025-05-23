package com.example.springplusteamproject.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.store.service.StoreTransactionalService;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;

@ExtendWith(MockitoExtension.class)
public class StoreTransactionalServiceTest {

    @InjectMocks
    private StoreTransactionalService storeTransactionalService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    private final CustomUserPrincipal principal = new CustomUserPrincipal(
        User.builder()
            .id(1L)
            .email("test@example.com")
            .userRole(UserRole.CUSTOMER)
            .isDeleted(false)
            .build()
    );

    @Test
    void 가게_생성_유저없음_예외() {
        // given
        StoreRequestDto dto = new StoreRequestDto(
            "장미 화원",
            "서울시 강서구",
            "image.png",
            "010-0000-0000",
            10000L,
            "09:00",
            "18:00"
        );

        when(userRepository.existsByEmail(principal.getUsername()))
            .thenReturn(false);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () ->
            storeTransactionalService.saveStore(dto, principal)
        );

        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 가게_생성_금지어_예외() {
        StoreRequestDto dto = new StoreRequestDto(
            "운영자 카페",
            "서울시 강남구",
            "image.png",
            "010-1234-5678",
            10000L,
            "09:00",
            "21:00"
        );

        when(userRepository.existsByEmail(principal.getUsername())).thenReturn(true);
        when(storeRepository.existsByUserIdAndDeletedFalse(principal.getId())).thenReturn(false);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () ->
            storeTransactionalService.saveStore(dto, principal)
        );

        assertEquals(ErrorStatus.STORE_BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    void 가게_생성_이름중복_예외() {
        StoreRequestDto dto = new StoreRequestDto(
            "안개꽃 화원",
            "대구 광역시",
            "image.png",
            "010-1234-5678",
            15000L,
            "09:00",
            "18:00"
        );

        when(userRepository.existsByEmail(principal.getUsername())).thenReturn(true);
        when(storeRepository.existsByUserIdAndDeletedFalse(principal.getId())).thenReturn(false);
        when(storeRepository.existsByNameAndDeletedFalse(dto.getName())).thenReturn(true);

        assertThrows(ApiException.class, () ->
            storeTransactionalService.saveStore(dto, principal)
        );
    }

    @Test
    void 가게_생성_가게_보유_예외() {
        StoreRequestDto dto = new StoreRequestDto(
            "튤립 화원",
            "부산광역시",
            "image.png",
            "010-5555-5555",
            20000L,
            "10:00",
            "19:00"
        );

        when(userRepository.existsByEmail(principal.getUsername())).thenReturn(true);
        when(storeRepository.existsByUserIdAndDeletedFalse(principal.getId())).thenReturn(true);

        assertThrows(ApiException.class, () ->
            storeTransactionalService.saveStore(dto, principal)
        );
    }
}
