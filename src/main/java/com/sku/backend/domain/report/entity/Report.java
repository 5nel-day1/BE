package com.sku.backend.domain.report.entity;

import com.sku.backend.domain.user.entity.User;
import com.sku.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "group_id")
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Building building;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Floor floor;

    @Column(nullable = false, length = 100)
    private String spaceDetail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "resolve_content", columnDefinition = "TEXT")
    private String resolveContent;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Builder
    public Report(User student, Building building, Floor floor, String spaceDetail, String content, String photoUrl) {
        this.student = student;
        this.building = building;
        this.floor = floor;
        this.spaceDetail = spaceDetail;
        this.content = content;
        this.photoUrl = photoUrl;
        this.status = ReportStatus.RECEIVED;
    }

    public String getReceiptNumber() {
        if (this.id == null) {
            return "S-0001";
        }
        return String.format("S-%04d", this.id);
    }

    public String getTitle() {
        return String.format("%s %s %s", building.getDescription(), floor.getDescription(), spaceDetail);
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
        if (status == ReportStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }
}