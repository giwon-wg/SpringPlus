package com.example.springplusteamproject.store.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

class StoreRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 검증_성공() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            "서울특별시",
            "image.png",
            "010-1234-5678",
            10000L,
            "09:00",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void 이름_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            null,
            "서울특별시",
            "image.png",
            "010-1234-5678",
            10000L,
            "09:00",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("가게 이름은 필수입니다."));
    }

    @Test
    void 주소_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            null,
            "image.png",
            "010-1234-5678",
            10000L,
            "09:00",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("주소는 필수입니다."));
    }

    @Test
    void 전화번호_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            null,
            "image.png",
            null,
            10000L,
            "09:00",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("전화번호는 필수입니다."));
    }

    @Test
    void 최소주문금액_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            "서울특별시",
            "image.png",
            "010-1234-5678",
            null,
            "09:00",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("최소 주문 금액은 필수입니다."));
    }

    @Test
    void 오픈시간_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            "서울특별시",
            "image.png",
            "010-1234-5678",
            10000L,
            " ",
            "18:00"
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("오픈 시간은 필수입니다."));
    }

    @Test
    void 마감시간_null_실패() {
        StoreRequestDto dto = new StoreRequestDto(
            "이쁜화원",
            "서울특별시",
            "image.png",
            "010-1234-5678",
            10000L,
            "09:00",
            " "
        );

        Set<ConstraintViolation<StoreRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("마감 시간은 필수입니다."));
    }

}
