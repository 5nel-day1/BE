package com.sku.backend.domain.report.repository;

import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByStudentIdOrderByIdDesc(Long studentId);

    List<Report> findAllByOrderByIdDesc();

    List<Report> findAllByStatusOrderByIdDesc(ReportStatus status);

    long countByStudentStudentId(String studentId);

    long countByStudentStudentIdAndStatus(String studentId, ReportStatus status);
}