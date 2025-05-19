package com.example.springplusteamproject.domain.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscountCouponRequestDto {

    @Schema(description = "쿠폰 이름", example = "5000원 할인")
    @NotBlank(message = "쿠폰 이름은 필수값입니다.")
    private final String couponName;

    @Schema(description = "할인값", example = "5000")
    @NotNull(message = "할인값은 필수값입니다.")
    private final Long discount;

    @Schema(description = "시작일", example = "2025-05-19T12:30:00")
    @NotNull(message = "시작일은 필수값입니다.")
    private final LocalDateTime issuedAt;

    @Schema(description = "만료일", example = "2025-05-26T12:30:00")
    @NotNull(message = "만료일은 필수값입니다.")
    private final LocalDateTime expiresAt;

    @Schema(description = "발급 수량", example = "100")
    @NotNull(message = "발급 수량은 필수값입니다.")
    private final Long quantity;
}
