package com.sku.backend.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportLikeResponseDto {
    private boolean liked;
    private int likeCount;

    public static ReportLikeResponseDto of(boolean liked, int likeCount) {
        return ReportLikeResponseDto.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }
}