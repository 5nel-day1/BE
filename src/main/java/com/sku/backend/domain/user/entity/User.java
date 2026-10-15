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

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 20, unique = true)
    private String studentId;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(length = 20, unique = true)
    private String adminCode;

    @Column(length = 50)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 255)
    private String profileImageUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean isStatusAlarm = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean isCommentAlarm = true;

    @Builder.Default
    @Column(nullable = false)
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