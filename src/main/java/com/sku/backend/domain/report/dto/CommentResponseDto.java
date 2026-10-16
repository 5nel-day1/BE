package com.sku.backend.domain.report.dto;

import com.sku.backend.domain.report.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private String anonymousName;
    private String content;
    private String createdAt;
    private boolean myComment;

    public static CommentResponseDto of(Comment comment, int anonymousIndex, String currentStudentId) {
        boolean isMy = comment.getUser().getStudentId().equals(currentStudentId);

        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .anonymousName("익명 " + anonymousIndex)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm")))
                .myComment(isMy)
                .build();
    }
}