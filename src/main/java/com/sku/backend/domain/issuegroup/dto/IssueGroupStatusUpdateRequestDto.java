package com.sku.backend.domain.issuegroup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueGroupStatusUpdateRequestDto {
    private String status; // IN_PROGRESS, RESOLVED, CANCELLED
}