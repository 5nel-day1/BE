package com.sku.backend.domain.auth.controller;

import com.sku.backend.domain.auth.dto.AdminLoginRequestDto;
import com.sku.backend.domain.auth.dto.AdminSignupRequestDto;
import com.sku.backend.domain.auth.dto.AuthResponseDto;
import com.sku.backend.domain.auth.dto.StudentLoginRequestDto;
import com.sku.backend.domain.auth.dto.StudentSignupRequestDto;
import com.sku.backend.domain.auth.service.AuthService;
import com.sku.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "학생 및 관리자 인증/회원가입 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "학생 회원가입", description = "학생 정보로 회원가입을 진행하고 사용자 정보를 반환합니다.")
    @PostMapping("/signup/student")
    public ResponseEntity<BaseResponse<AuthResponseDto>> studentSignup(
            @Valid @RequestBody StudentSignupRequestDto requestDto) {
        AuthResponseDto response = authService.studentSignup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "학생 회원가입에 성공했습니다.", response));
    }

    @Operation(summary = "관리자 회원가입", description = "관리자 정보로 회원가입을 진행하고 사용자 정보를 반환합니다.")
    @PostMapping("/signup/admin")
    public ResponseEntity<BaseResponse<AuthResponseDto>> adminSignup(
            @Valid @RequestBody AdminSignupRequestDto requestDto) {
        AuthResponseDto response = authService.adminSignup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "관리자 회원가입에 성공했습니다.", response));
    }

    @Operation(summary = "학생 로그인", description = "학번과 비밀번호로 로그인하고 사용자 정보를 반환합니다.")
    @PostMapping("/login/student")
    public ResponseEntity<BaseResponse<AuthResponseDto>> studentLogin(
            @Valid @RequestBody StudentLoginRequestDto requestDto) {
        AuthResponseDto response = authService.studentLogin(requestDto);
        return ResponseEntity.ok(BaseResponse.success("학생 로그인에 성공했습니다.", response));
    }

    @Operation(summary = "관리자 로그인", description = "관리자 코드와 비밀번호로 로그인하고 사용자 정보를 반환합니다.")
    @PostMapping("/login/admin")
    public ResponseEntity<BaseResponse<AuthResponseDto>> adminLogin(
            @Valid @RequestBody AdminLoginRequestDto requestDto) {
        AuthResponseDto response = authService.adminLogin(requestDto);
        return ResponseEntity.ok(BaseResponse.success("관리자 로그인에 성공했습니다.", response));
    }
}