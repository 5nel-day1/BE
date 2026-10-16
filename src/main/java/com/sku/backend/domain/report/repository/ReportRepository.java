package com.sku.backend.domain.report.repository;

import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByStudentIdOrderByIdDesc(Long studentId);

    List<Report> findAllByOrderByIdDesc();

    List<Report> findAllByStatusOrderByIdDesc(ReportStatus status);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, ReportStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Report r SET r.student = null WHERE r.student.id = :studentId")
    void updateStudentToNullByStudentId(@Param("studentId") Long studentId);
}