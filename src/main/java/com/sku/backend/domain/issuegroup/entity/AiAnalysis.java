package com.sku.backend.domain.issuegroup.entity;

import com.sku.backend.domain.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_group_id")
    private IssueGroup issueGroup;

    private String building;
    private String floor;
    private String facility;
    private String problemType;
    private String severity;
    private String summaryTitle;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    private Integer urgencyScore;
}