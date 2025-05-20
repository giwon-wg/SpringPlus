package com.example.springplusteamproject.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Setter
@AllArgsConstructor
@Jacksonized
public class OrderItemRequestDTO {
    @Schema(description = "꽃 ID", example = "1L")
    @NotNull(message = "꽃은 필수입니다.")
    private final Long flowerId;
    @Schema(description = "수량", example = "1")
    @NotNull(message = "수량은 필수입니다.")
    @Min(value=1, message="수량은 1개 이상입니다.")
    private final Integer quantity; //메뉴 한건 수량
}
