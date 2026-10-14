package com.sku.backend.domain.report.entity;

public enum ReportStatus {
    RECEIVED,   // 접수
    CHECKING,   // 확인 중
    IN_PROGRESS,// 처리 중
    RESOLVED    // 해결 완료
}