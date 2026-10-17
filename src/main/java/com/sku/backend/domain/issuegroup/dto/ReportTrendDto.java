package com.sku.backend.domain.issuegroup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTrendDto {
    private String date; // "yyyy-MM-dd"
    private long count;
}