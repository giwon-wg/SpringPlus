package com.example.springplusteamproject.user.entity;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserUnitTest {


    @Test
    void 회원_삭제_요청시_회원의_isDeleted가_true면_에러코드_DELETED_USER() {
        // given
        User user = User.builder().isDeleted(true).build();

        // when
        ApiException apiException = assertThrows(ApiException.class, user::validateDelete);

        // then
        assertEquals(ErrorStatus.DELETED_USER, apiException.getErrorCode());
    }
}
