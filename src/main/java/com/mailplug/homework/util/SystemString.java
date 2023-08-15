package com.mailplug.homework.util;

import lombok.Getter;

@Getter
public enum SystemString {
    // request 관련
    INVALID_INPUT("잘못된 입렵입니다.", false),

    // response 관련

    // DB 관련
    NOT_FOUND_BOARD("해당 게시판을 찾을 수 없습니다.", false),
    NOT_FOUND_BOARD_DETAIL("해당 게시물을 찾을 수 없습니다.", false),

    // 로직 관련
    DO_NOT_MATCH_USERNAME("게시물 수정 권한이 없습니다", false),
    ALREADY_DELETED("이미 지워진 게시물입니다.", false);


    private final String message;
    private final boolean isSuccess;

    SystemString(String message, boolean isSuccess) {
        this.message = message;
        this.isSuccess = isSuccess;
    }
}
