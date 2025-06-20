package com.expis.community.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 게시판 관련
    BOARD_LIST_FAIL("B001", "게시글 목록을 불러오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BOARD_NOT_FOUND("B002", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOARD_CREATE_FAIL("B003", "게시글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BOARD_MODIFY_FAIL("B004", "게시글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BOARD_DELETE_FAIL("B005", "게시글 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 댓글 관련
    COMMENT_CREATE_FAIL("C001", "댓글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_MODIFY_FAIL("C002", "댓글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_DELETE_FAIL("C003", "댓글 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 관련사이트 관련
    RELATED_SITE_LIST_FAIL("R001", "관련사이트 목록을 불러오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    RELATED_SITE_LIST_EMPTY("R002", "조회된 관련 사이트가 없습니다.", HttpStatus.NOT_FOUND),
    RELATED_SITE_MENU_NOT_FOUND("R003", "게시판 메뉴 리스트가 없습니다.", HttpStatus.NOT_FOUND),
    RELATED_SITE_NOT_FOUND("B004", "관련사이트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RELATED_SITE_CREATE_FAIL("R005", "관련사이트 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    RELATED_SITE_MODIFY_FAIL("R006", "관련사이트 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    RELATED_SITE_DELETE_FAIL("R007", "관련사이트 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 파일 관련
    FILE_DOWNLOAD_FAIL("F001", "파일 다운로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_FAIL("F002", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_INVALID("F003", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),

    // DB 관련
    DB_ERROR("D001", "데이터베이스 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 공통
    INVALID_INPUT("C001", "입력값이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    SESSION_NOT_FOUND("C002", "해당 세션 값이 없습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("C999", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status){
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
