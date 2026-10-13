package com.sku.backend.domain.user.entity;

import com.sku.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 이름
    @Column(nullable = false, length = 50)
    private String name;

    // 사용자 권한 (STUDENT, ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 학교 이메일 (학생만 필수, unique)
    @Column(length = 100, unique = true)
    private String email;

    // 학번 (학생만 필수, unique)
    @Column(name = "student_id", length = 20, unique = true)
    private String studentId;

    // 학과 (학생) 또는 소속 부서 (관리자)
    @Column(nullable = false, length = 50)
    private String department;

    // 관리자 코드 (관리자만 필수, unique)
    @Column(name = "admin_code", length = 20, unique = true)
    private String adminCode;

    // 전화번호 (관리자만 필수)
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    // 비밀번호
    @Column(nullable = false, length = 255)
    private String password;

    // 닉네임
    @Column(length = 50)
    private String nickname;

    // 프로필 이미지 URL
    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    // 처리 상태 변경 알림 여부
    @Builder.Default
    @Column(name = "is_status_alarm", nullable = false)
    private boolean isStatusAlarm = true;

    // 댓글 알림 여부
    @Builder.Default
    @Column(name = "is_comment_alarm", nullable = false)
    private boolean isCommentAlarm = true;

    // 해결 소식 알림 여부
    @Builder.Default
    @Column(name = "is_solved_alarm", nullable = false)
    private boolean isSolvedAlarm = true;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateStatusAlarm(boolean isStatusAlarm) {
        this.isStatusAlarm = isStatusAlarm;
    }

    public void updateCommentAlarm(boolean isCommentAlarm) {
        this.isCommentAlarm = isCommentAlarm;
    }

    public void updateSolvedAlarm(boolean isSolvedAlarm) {
        this.isSolvedAlarm = isSolvedAlarm;
    }
}