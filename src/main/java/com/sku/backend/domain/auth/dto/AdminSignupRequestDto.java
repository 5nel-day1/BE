package com.sku.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "관리자 회원가입 요청")
public class AdminSignupRequestDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotBlank(message = "관리자 코드는 필수 입력 값입니다.")
    @Schema(description = "관리자 코드", example = "0000")
    private String adminCode;

    @NotBlank(message = "소속은 필수 입력 값입니다.")
    @Schema(description = "소속", example = "시설미화관리담당")
    private String department;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    @Schema(description = "비밀번호 확인", example = "password123")
    private String passwordConfirm;

    @AssertTrue(message = "필수 서비스 이용약관 및 개인정보 수집·이용에 동의해야 합니다.")
    @Schema(description = "약관 동의 여부", example = "true")
    private boolean termsAgreed;
}