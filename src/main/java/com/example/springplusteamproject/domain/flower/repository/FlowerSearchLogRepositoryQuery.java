package com.example.springplusteamproject.domain.flower.repository;

import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import java.util.List;

public interface FlowerSearchLogRepositoryQuery {

    List<FlowerSearchResponseDto> getTop10Keywords(Integer year, Integer month, Integer day);
}
