package com.port.portcoin.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {

    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION",
            "데이터 처리 중 문제가 발생했습니다. 요청을 확인하고 다시 시도해주세요"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "요청 값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
            "서버에서 문제가 발생하였습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "ALREADY_DELETED", "탈퇴된 사용자입니다."),
    EMAIL_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "EMAIL_PASSWORD_MISMATCH",
            "이메일 혹은 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_OLD", "새 비밀번호가 기존 비밀번호와 동일합니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_USER", "인증되지 않은 사용자입니다."),
    NICKNAME_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "NICKNAME_SAME_AS_OLD", "새 닉네임이 기존 닉네임과 동일합니다."),
    NOT_ADMIN_ROLE(HttpStatus.BAD_REQUEST, "NOT_ADMIN_ROLE", "관리자만 접근할 수 있습니다."),

    // 리프레시 토큰 관련
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "잘못된 리프레시 토큰입니다."),

    //포트폴리오 관련
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "포트폴리오를 찾을 수 없습니다."),

    //코인 관련
    COIN_NOT_FOUND(HttpStatus.NOT_FOUND,"COIN_NOT_FOUND", "코인을 확인할 수 없습니다."),
    PORTFOLIO_COIN_NOT_FOUND(HttpStatus.NOT_FOUND, "COIN_NOT_FOUND", "포트폴리오에 등록된 코인을 찾을 수 없습니다." ),

    //외부 API 관련
    CHECK_API_KEY(HttpStatus.NOT_FOUND, "CHECK_API_KEY","API KEY를 확인해주세요.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ExceptionEnum(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
