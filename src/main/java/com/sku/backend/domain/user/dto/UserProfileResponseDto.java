package com.sku.backend.domain.user.dto;

import com.sku.backend.domain.user.entity.Role;
import com.sku.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {

    private String name;
    private Role role;
    private String department;
    private String studentId;
    private String email;

    private long totalReports;    // 내 제보 수
    private long solvedReports;   // 해결 완료 수

    private boolean isStatusAlarm;
    private boolean isCommentAlarm;
    private boolean isSolvedAlarm;

    public static UserProfileResponseDto of(User user, long totalReports, long solvedReports) {
        return UserProfileResponseDto.builder()
                .name(user.getName())
                .role(user.getRole())
                .department(user.getDepartment())
                .studentId(user.getStudentId())
                .email(user.getEmail())
                .totalReports(totalReports)
                .solvedReports(solvedReports)
                .isStatusAlarm(user.isStatusAlarm())
                .isCommentAlarm(user.isCommentAlarm())
                .isSolvedAlarm(user.isSolvedAlarm())
                .build();
    }
}