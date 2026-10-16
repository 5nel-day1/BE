package com.sku.backend.domain.report.controller;

import com.sku.backend.domain.report.dto.CommentCreateRequestDto;
import com.sku.backend.domain.report.dto.CommentResponseDto;
import com.sku.backend.domain.report.service.CommentService;
import com.sku.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "제보 글 댓글 API")
@RestController
@RequestMapping("/api/reports/{reportId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회", description = "특정 제보 글의 댓글 목록을 조회하며 익명 번호와 본인 작성 여부를 반환합니다.")
    @GetMapping
    public BaseResponse<List<CommentResponseDto>> getComments(
            @PathVariable Long reportId,
            @RequestParam String studentId
    ) {
        List<CommentResponseDto> responseDtos = commentService.getComments(reportId, studentId);
        return BaseResponse.success(responseDtos);
    }

    @Operation(summary = "댓글 작성", description = "특정 제보 글에 새로운 댓글을 등록합니다.")
    @PostMapping
    public BaseResponse<Void> createComment(
            @PathVariable Long reportId,
            @RequestParam String studentId,
            @RequestBody CommentCreateRequestDto requestDto
    ) {
        commentService.createComment(reportId, studentId, requestDto);
        return BaseResponse.success(null);
    }

    @Operation(summary = "댓글 삭제", description = "작성자 본인인 경우에만 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public BaseResponse<Void> deleteComment(
            @PathVariable Long reportId,
            @PathVariable Long commentId,
            @RequestParam String studentId
    ) {
        commentService.deleteComment(commentId, studentId);
        return BaseResponse.success(null);
    }
}