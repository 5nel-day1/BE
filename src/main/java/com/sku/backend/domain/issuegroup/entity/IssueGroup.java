package com.sku.backend.domain.issuegroup.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class IssueGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String building;
    private String facility;
    private String problemType;
    private String status;

    private String resolutionContent; // 처리 결과 내용
    private LocalDateTime resolvedAt; // 해결 완료 일시

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateResolution(String resolutionContent, LocalDateTime resolvedAt) {
        this.resolutionContent = resolutionContent;
        this.resolvedAt = resolvedAt;
    }
}