package com.example.springplusteamproject.temp;

import lombok.Getter;

@Getter
public class UserDummy {
    private final Long id = 1L;
    private final String name = "꽃파는사람";

    public static final UserDummy INSTANCE = new UserDummy();

    private UserDummy() {}
}
