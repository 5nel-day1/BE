package com.sku.backend.domain.report.service;

import com.sku.backend.domain.report.dto.CommentCreateRequestDto;
import com.sku.backend.domain.report.dto.CommentResponseDto;
import com.sku.backend.domain.report.entity.Comment;
import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.repository.CommentRepository;
import com.sku.backend.domain.report.repository.ReportRepository;
import com.sku.backend.domain.user.entity.User;
import com.sku.backend.domain.user.repository.UserRepository;
import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public List<CommentResponseDto> getComments(Long reportId, Long userId) {
        if (!reportRepository.existsById(reportId)) {
            throw new CustomException(GlobalErrorCode.REPORT_NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findAllByReportIdOrderByIdAsc(reportId);

        if (comments.isEmpty()) {
            return List.of();
        }

        Long firstCommentId = comments.get(0).getId();

        return comments.stream()
                .map(comment -> {
                    int anonymousIndex = (int) (comment.getId() - firstCommentId + 1);
                    return CommentResponseDto.of(comment, anonymousIndex, userId);
                })
                .toList();
    }

    @Transactional
    public void createComment(Long reportId, Long userId, CommentCreateRequestDto requestDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.REPORT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .report(report)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(GlobalErrorCode.COMMENT_ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}