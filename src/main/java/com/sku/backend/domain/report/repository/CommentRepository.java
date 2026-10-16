package com.sku.backend.domain.report.repository;

import com.sku.backend.domain.report.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByReportIdOrderByIdAsc(Long reportId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.user = null WHERE c.user.id = :userId")
    void updateUserToNullByUserId(@Param("userId") Long userId);
}