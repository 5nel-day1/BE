package com.sku.backend.domain.report.dto;

import com.sku.backend.domain.report.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponseDto {

    @Schema(description = "제보 식별자 ID", example = "1")
    private Long id;

    @Schema(description = "접수번호", example = "S-1000")
    private String receiptNumber;

    @Schema(description = "목록 카드 제목", example = "유담관 4층 화장실 세면대")
    private String title;

    @Schema(description = "제보자 이름", example = "홍길동")
    private String studentName;

    @Schema(description = "내용", example = "세면대 물이 너무 천천히 내려가요.")
    private String content;

    @Schema(description = "사진 URL")
    private String photoUrl;

    @Schema(description = "처리 상태", example = "RECEIVED")
    private String status;

    @Schema(description = "공감 개수", example = "4")
    private int likeCount;

    @Schema(description = "처리 결과 내용 (해결 완료 시)", example = "핸드타올 디스펜서를 수리하고 비누를 보충했습니다.")
    private String resolveContent;

    @Schema(description = "해결 완료 일시", example = "2026-10-14T12:22:00")
    private LocalDateTime resolvedAt;

    @Schema(description = "접수 일시", example = "2026-10-14T12:22:00")
    private LocalDateTime createdAt;

    public static ReportResponseDto from(Report report) {
        String authorName = (report.getStudent() != null) ? report.getStudent().getName() : "탈퇴한 회원";

        return ReportResponseDto.builder()
                .id(report.getId())
                .receiptNumber(report.getReceiptNumber())
                .title(report.getTitle())
                .studentName(authorName)
                .content(report.getContent())
                .photoUrl(report.getPhotoUrl())
                .status(report.getStatus().name())
                .likeCount(report.getLikeCount())
                .resolveContent(report.getResolveContent())
                .resolvedAt(report.getResolvedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }
}