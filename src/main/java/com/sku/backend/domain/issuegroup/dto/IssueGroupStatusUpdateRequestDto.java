package com.sku.backend.domain.issuegroup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueGroupStatusUpdateRequestDto {
    private String status; // IN_PROGRESS, RESOLVED, CANCELLED
    private String resolutionContent; // 관리자가 입력한 처리 결과 내용
}