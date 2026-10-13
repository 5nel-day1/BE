package com.sku.backend.global.exception;

import com.sku.backend.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    // 공통 에러 (G)
    INVALID_INPUT_VALUE("G001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("G002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("G003", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED("G004", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    TYPE_MISMATCH("G005", "요청 인자의 데이터 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 인증 / 회원 도메인 에러코드 (A / U)
    UNAUTHORIZED("A001", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("A002", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("A003", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("A004", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("A005", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("A006", "기존 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    STUDENT_ID_ALREADY_EXISTS("A007", "이미 존재하는 학번입니다.", HttpStatus.CONFLICT),
    ADMIN_CODE_ALREADY_EXISTS("A008", "이미 존재하는 관리자 코드입니다.", HttpStatus.CONFLICT),

    // 제보 도메인 에러코드 (R)
    REPORT_NOT_FOUND("R001", "존재하지 않는 제보입니다.", HttpStatus.NOT_FOUND),
    REPORT_ACCESS_DENIED("R002", "해당 제보에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 댓글 도메인 에러코드 (C)
    COMMENT_NOT_FOUND("C001", "존재하지 않는 댓글입니다.", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED("C002", "해당 댓글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 문제군(IssueGroup) 도메인 에러코드 (I)
    ISSUE_GROUP_NOT_FOUND("I001", "존재하지 않는 문제 그룹입니다.", HttpStatus.NOT_FOUND),

    // 파일 업로드 도메인 에러코드 (F)
    FILE_UPLOAD_FAILED("F001", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_IMAGE_TYPE("F002", "지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 허용)", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}