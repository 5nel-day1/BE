package com.sku.backend.domain.issuegroup.controller;

import com.sku.backend.domain.issuegroup.dto.IssueGroupDetailResponseDto;
import com.sku.backend.domain.issuegroup.dto.IssueGroupResponseDto;
import com.sku.backend.domain.issuegroup.dto.IssueGroupStatusUpdateRequestDto;
import com.sku.backend.domain.issuegroup.service.IssueGroupService;
import com.sku.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Issue Group", description = "관리자 전용 AI 이슈그룹 대시보드 API")
@RestController
@RequestMapping("/api/admin/issue-groups")
@RequiredArgsConstructor
public class AdminIssueGroupController {

    private final IssueGroupService issueGroupService;

    @Operation(summary = "점검 필요도순 이슈 목록 조회", description = "AI가 분석한 점검 필요도가 높은 순서대로 관리자 대시보드 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse<List<IssueGroupResponseDto>>> getAdminIssueGroups() {
        List<IssueGroupResponseDto> result = issueGroupService.getAdminIssueGroups();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "이슈 그룹 처리 상태 변경", description = "관리자가 이슈의 처리 상태 및 결과를 변경합니다.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<IssueGroupDetailResponseDto>> updateIssueGroupStatus(
            @PathVariable Long id,
            @RequestBody IssueGroupStatusUpdateRequestDto requestDto) {

        IssueGroupDetailResponseDto responseDto = issueGroupService.updateIssueGroupStatus(id, requestDto.getStatus(), requestDto.getResolutionContent());

        return ResponseEntity.ok(BaseResponse.success("상태가 성공적으로 변경되었습니다.", responseDto));
    }
}