package com.sku.backend.domain.user.service;

import com.sku.backend.domain.report.entity.ReportStatus;
import com.sku.backend.domain.report.repository.ReportRepository;
import com.sku.backend.domain.user.dto.NotificationPatchRequestDto;
import com.sku.backend.domain.user.dto.UserProfileResponseDto;
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
public class UserService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // 마이페이지 프로필 조회
    public UserProfileResponseDto getUserProfile(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        long totalReports = reportRepository.countByStudentStudentId(studentId);
        long resolvedReports = reportRepository.countByStudentStudentIdAndStatus(studentId, ReportStatus.RESOLVED);

        return UserProfileResponseDto.of(user, totalReports, resolvedReports);
    }

    // 알림 설정 토글 수정
    @Transactional
    public void updateNotificationSettings(String studentId, NotificationPatchRequestDto requestDto) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        user.updateStatusAlarm(requestDto.isStatusAlarm());
        user.updateCommentAlarm(requestDto.isCommentAlarm());
        user.updateSolvedAlarm(requestDto.isSolvedAlarm());
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}