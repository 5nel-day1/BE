package com.sku.backend.domain.issuegroup.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class IssueGroupDetailResponseDto {
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
    private List<String> reportContents; // 그룹에 묶인 제보들 목록
}