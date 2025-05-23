package com.example.springplusteamproject.domain.flower.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlowerSearchResponseDto {

    private int rank;

    private String keyword;

    private int count;
}
