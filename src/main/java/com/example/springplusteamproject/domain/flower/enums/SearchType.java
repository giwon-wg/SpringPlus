package com.example.springplusteamproject.domain.flower.enums;

import java.time.LocalDate;
import java.time.YearMonth;

public enum SearchType {

    DAILY {
        @Override
        public String getRedisKey() {
            return "popular:keywords:daily:" + LocalDate.now();
        }
    },
    MONTHLY {
        @Override
        public String getRedisKey() {
            return "popular:keywords:monthly:" + YearMonth.now();
        }
    },
    YEARLY {
        @Override
        public String getRedisKey() {
            return "popular:keywords:yearly:" + LocalDate.now().getYear();
        }
    };

    public abstract String getRedisKey();
}
