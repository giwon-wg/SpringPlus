package com.example.springplusteamproject.domain.flower.service;

import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import com.example.springplusteamproject.domain.flower.enums.SearchType;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Top10KeywordsScheduler {

    private final StringRedisTemplate redisTemplate;
    private final FlowerService flowerService;

    // 매일 자정 어제 인기 검색 키워드 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteDailyKeywordData() {
        String yesterdayKey = "popular:keywords:daily:" + LocalDate.now().minusDays(1);
        redisTemplate.delete(yesterdayKey);
    }

    // 매달 1일 자정에 지난 달 인기 검색 키워드 삭제
    @Scheduled(cron = "0 0 0 1 * *")
    public void deleteMonthlyKeywordData() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String monthlyKey = "popular:keywords:monthly:" + lastMonth;
        redisTemplate.delete(monthlyKey);
    }

    // 매년 1월 1일 자정에 전년도 인기 검색 키워드 삭제
    @Scheduled(cron = "0 0 0 1 1 *")
    public void deleteYearlyKeywordData() {
        int lastYear = Year.now().getValue() - 1;
        String yearlyKey = "popular:keywords:yearly:" + lastYear;
        redisTemplate.delete(yearlyKey);
    }

    // 인기검색어로 검색한 결과 미리 캐싱
    @Scheduled(cron = "0 15 * * * ?")
    public void preloadTop10SearchResults() {
        for (SearchType type : SearchType.values()) {
            List<FlowerSearchResponseDto> topKeywords = flowerService.getTop10Keywords(type);

            for (FlowerSearchResponseDto dto : topKeywords) {
                flowerService.searchFlowersV2(dto.getKeyword(), null, 1, 10);
            }
        }
    }

    // 최초 실행
    @PostConstruct
    public void preloadOnStartup() {
        preloadTop10SearchResults();
    }
}


