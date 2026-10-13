package com.sku.backend.domain.auth.service;

import com.sku.backend.domain.auth.dto.AdminLoginRequestDto;
import com.sku.backend.domain.auth.dto.AdminSignupRequestDto;
import com.sku.backend.domain.auth.dto.AuthResponseDto;
import com.sku.backend.domain.auth.dto.StudentLoginRequestDto;
import com.sku.backend.domain.auth.dto.StudentSignupRequestDto;
import com.sku.backend.domain.user.entity.Role;
import com.sku.backend.domain.user.entity.User;
import com.sku.backend.domain.user.repository.UserRepository;
import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    // 학생 회원가입
    @Transactional
    public AuthResponseDto studentSignup(StudentSignupRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
        }

        String fullEmail = requestDto.getEmail().contains("@")
                ? requestDto.getEmail()
                : requestDto.getEmail() + "@skuniv.ac.kr";

        if (userRepository.findByStudentId(requestDto.getStudentId()).isPresent()) {
            throw new CustomException(GlobalErrorCode.STUDENT_ID_ALREADY_EXISTS);
        }
        if (userRepository.findByEmail(fullEmail).isPresent()) {
            throw new CustomException(GlobalErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(requestDto.getName())
                .role(Role.STUDENT)
                .studentId(requestDto.getStudentId())
                .email(fullEmail)
                .department(requestDto.getDepartment())
                .password(requestDto.getPassword())
                .isStatusAlarm(true)
                .isCommentAlarm(true)
                .isSolvedAlarm(true)
                .build();

        User savedUser = userRepository.save(user);
        return AuthResponseDto.from(savedUser);
    }

    // 학생 로그인
    public AuthResponseDto studentLogin(StudentLoginRequestDto requestDto) {
        User user = userRepository.findByStudentId(requestDto.getStudentId())
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new CustomException(GlobalErrorCode.INVALID_CREDENTIALS);
        }

        return AuthResponseDto.from(user);
    }

    // 관리자 회원가입
    @Transactional
    public AuthResponseDto adminSignup(AdminSignupRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(GlobalErrorCode.INVALID_INPUT_VALUE);
        }

        if (userRepository.findByAdminCode(requestDto.getAdminCode()).isPresent()) {
            throw new CustomException(GlobalErrorCode.ADMIN_CODE_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(requestDto.getName())
                .role(Role.ADMIN)
                .adminCode(requestDto.getAdminCode())
                .phoneNumber(requestDto.getPhoneNumber())
                .department(requestDto.getDepartment())
                .password(requestDto.getPassword())
                .isStatusAlarm(true)
                .isCommentAlarm(true)
                .isSolvedAlarm(true)
                .build();

        User savedUser = userRepository.save(user);
        return AuthResponseDto.from(savedUser);
    }

    // 관리자 로그인
    public AuthResponseDto adminLogin(AdminLoginRequestDto requestDto) {
        User user = userRepository.findByAdminCode(requestDto.getAdminCode())
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new CustomException(GlobalErrorCode.INVALID_CREDENTIALS);
        }

        return AuthResponseDto.from(user);
    }
}