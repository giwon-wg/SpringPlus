package com.example.springplusteamproject.common.config;

import java.util.Set;

public class ForbiddenWordUtil {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
        "어드민", "Admin", "운영자"
    );

    public static boolean containsForbiddenWord(String input) {
        return FORBIDDEN_WORDS.stream().anyMatch(input::contains);
    }
}
