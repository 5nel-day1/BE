package com.sku.backend.domain.report.repository;

import com.sku.backend.domain.report.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByReportIdOrderByIdAsc(Long reportId);
}