package com.port.portcoin.common.response;
import lombok.Getter;

@Getter
public enum ApiResponseEnum {

    SIGNUP_SUCCESS("회원가입 완료"),
    PROFILE_RETRIEVED_SUCCESS("프로필 조회가 성공적으로 완료되었습니다."),
    PASSWORD_CHANGED_SUCCESS("비밀번호 변경이 성공적으로 완료되었습니다."),
    NICKNAME_CHANGED_SUCCESS("닉네임 변경이 성공적으로 완료되었습니다."),
    USER_DELETED_SUCCESS("회원 탈퇴가 완료되었습니다."),

    CREATE_PORTFOLIO_SUCCESS("포트폴리오 생성이 완료되었습니다."),
    UPDATE_PORTFOLIO_SUCCESS("포트폴리오 이름 수정이 완료되었습니다."),
    DELETE_PORTFOLIO_SUCCESS("포트폴리오 삭제가 완료되었습니다."),

    REGISTRATION_SUCCESS("코인이 등록되었습니다."),
    GET_SUCCESS("조회 완료");


    private final String message;

    ApiResponseEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
