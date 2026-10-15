package com.sku.backend.domain.issuegroup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueGroupResponseDto {
    private Long id;
    private String title;
    private String building;
    private String floor;
    private String facility;
    private String problemType;
    private String severity;
    private Integer urgencyScore;
    private String aiSummary;
    private String status;
}