package com.sku.backend.domain.issuegroup.repository;

import com.sku.backend.domain.issuegroup.entity.AiAnalysis;
import com.sku.backend.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {

    List<AiAnalysis> findAllByOrderByUrgencyScoreDesc();

    List<AiAnalysis> findByIssueGroupId(Long issueGroupId);

    void deleteAllByReportIn(List<Report> reports);
}