package com.example.springplusteamproject.store.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.springplusteamproject.domain.store.dto.request.StoreCheckNameRequestDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class StoreCheckNameRequestDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 검증_성공() {
        StoreCheckNameRequestDto dto = new StoreCheckNameRequestDto(
            "이쁜화원"
        );

        Set<ConstraintViolation<StoreCheckNameRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void 이름_null_실패() {
        StoreCheckNameRequestDto dto = new StoreCheckNameRequestDto(
            null
        );

        Set<ConstraintViolation<StoreCheckNameRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().equals("가게 이름은 필수입니다."));
    }
}
