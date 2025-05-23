package com.example.springplusteamproject.domain.flower.repository;

import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import com.example.springplusteamproject.domain.flower.entity.QFlowerSearchLog;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowerSearchLogRepositoryQueryImpl implements FlowerSearchLogRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FlowerSearchResponseDto> getTop10Keywords(Integer year, Integer month, Integer day) {
        QFlowerSearchLog log = QFlowerSearchLog.flowerSearchLog;

        List<Tuple> top10 = jpaQueryFactory
            .select(log.keyword, log.count())
            .from(log)
            .where(
                eqYear(year),
                eqMonth(month),
                eqDay(day)
            )
            .groupBy(log.keyword)
            .orderBy(log.count().desc())
            .limit(10)
            .fetch();

        List<FlowerSearchResponseDto> dto = new ArrayList<>();
        int rank = 1;
        for (Tuple tuple : top10) {
            String keyword = tuple.get(log.keyword);
            int count = tuple.get(log.count()).intValue();
            dto.add(new FlowerSearchResponseDto(rank++, keyword, count));
        }

        return dto;
    }

    private BooleanExpression eqYear(Integer year) {
        return year != null ? QFlowerSearchLog.flowerSearchLog.year.eq(year) : null;
    }

    private BooleanExpression eqMonth(Integer month) {
        return month != null ? QFlowerSearchLog.flowerSearchLog.month.eq(month) : null;
    }

    private BooleanExpression eqDay(Integer day) {
        return day != null ? QFlowerSearchLog.flowerSearchLog.day.eq(day) : null;
    }

}
