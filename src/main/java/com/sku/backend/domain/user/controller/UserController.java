package com.sku.backend.domain.user.controller;

import com.sku.backend.domain.user.dto.NotificationPatchRequestDto;
import com.sku.backend.domain.user.dto.UserProfileResponseDto;
import com.sku.backend.domain.user.service.UserService;
import com.sku.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "마이페이지 및 회원 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "마이페이지 정보 조회", description = "학번을 기준으로 유저의 프로필, 내 제보 수, 해결 완료 수, 알림 설정 상태를 조회합니다.")
    @GetMapping("/me")
    public BaseResponse<UserProfileResponseDto> getUserProfile(@RequestParam String studentId) {
        UserProfileResponseDto responseDto = userService.getUserProfile(studentId);
        return BaseResponse.success(responseDto);
    }

    @Operation(summary = "알림 설정 변경", description = "학번을 기준으로 처리 상태 변경, 댓글, 해결 소식 알림 수신 토글 상태를 수정합니다.")
    @PatchMapping("/me/notifications")
    public BaseResponse<Void> updateNotificationSettings(
            @RequestParam String studentId,
            @RequestBody NotificationPatchRequestDto requestDto
    ) {
        userService.updateNotificationSettings(studentId, requestDto);
        return BaseResponse.success(null);
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public BaseResponse<Void> deleteUser(@RequestParam String studentId) {
        userService.deleteUser(studentId);
        return BaseResponse.success(null);
    }
}