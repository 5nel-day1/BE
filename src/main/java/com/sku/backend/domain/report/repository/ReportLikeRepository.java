package com.sku.backend.domain.report.repository;

import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.entity.ReportLike;
import com.sku.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportLikeRepository extends JpaRepository<ReportLike, Long> {
    Optional<ReportLike> findByReportAndUser(Report report, User user);
    boolean existsByReportAndUser(Report report, User user);

    void deleteAllByReportIn(List<Report> reports);

    void deleteAllByUserId(Long userId);
}