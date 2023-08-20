package com.mailplug.homework.util;

import lombok.Getter;

@Getter
public enum SystemString {
    // request 관련
    INVALID_INPUT("잘못된 입렵입니다.", false),

    // response 관련
    SUCCESS_CREATE_BOARD_DETAIL("성곡적으로 게시물을 생성했습니다", true),
    SUCCESS_READ_ONE_BOARD_DETAIL("성공적으로 게시물을 읽어왔습니다", true),
    SUCCESS_UPDATE_BOARD_DETAIL("성공적으로 게시물을 수정했습니다", true),
    SUCCESS_DELETE_BOARD_DETAIL("성공적으로 게시물을 삭제했습니다", true),
    SUCCESS_READ_ALL_BOARD_DETAIL("게시물을 불러왔습니다", true),


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
