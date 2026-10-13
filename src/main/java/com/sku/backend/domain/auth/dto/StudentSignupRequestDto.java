package com.sku.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "학생 회원가입 요청")
public class StudentSignupRequestDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotBlank(message = "학번은 필수 입력 값입니다.")
    @Schema(description = "학번", example = "2023123456")
    private String studentId;

    @NotBlank(message = "학과는 필수 입력 값입니다.")
    @Schema(description = "학과", example = "컴퓨터공학과")
    private String department;

    @NotBlank(message = "학교 이메일 아이디는 필수 입력 값입니다.")
    @Schema(description = "학교 이메일 아이디 (@skuniv.ac.kr 앞부분)", example = "user")
    private String email;

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