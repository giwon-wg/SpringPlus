package com.example.springplusteamproject.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class StoreRequestDto {

    @Schema(description = "가게 이름", example = "이쁜화원")
    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @Schema(description = "가게 주소", example = "서울특별시")
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Schema(description = "가게 이미지")
    private String image;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @Schema(description = "최소 주문 금액", example = "10000")
    @NotNull(message = "최소 주문 금액은 필수입니다.")
    private Long minOrderPrice;

    @Schema(description = "오픈 시간", example = "09:00")
    @NotBlank(message = "오픈 시간은 필수입니다.")
    private String openTime;

    @Schema(description = "마감 시간", example = "18:00")
    @NotBlank(message = "마감 시간은 필수입니다.")
    private String closeTime;

}
