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

    // 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long reportId, String studentId) {
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
                    return CommentResponseDto.of(comment, anonymousIndex, studentId);
                })
                .toList();
    }

    // 댓글 작성
    @Transactional
    public void createComment(Long reportId, String studentId, CommentCreateRequestDto requestDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.REPORT_NOT_FOUND));

        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .report(report)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String studentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getStudentId().equals(studentId)) {
            throw new CustomException(GlobalErrorCode.COMMENT_ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}