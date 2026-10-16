package com.sku.backend.domain.issuegroup.controller;

import com.sku.backend.domain.issuegroup.dto.IssueGroupDetailResponseDto;
import com.sku.backend.domain.issuegroup.dto.IssueGroupResponseDto;
import com.sku.backend.domain.issuegroup.service.IssueGroupService;
import com.sku.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Issue Group", description = "캠퍼스 이슈 그룹 API")
@RestController
@RequestMapping("/api/issue-groups")
@RequiredArgsConstructor
public class IssueGroupController {

    private final IssueGroupService issueGroupService;

    @Operation(summary = "전체 이슈 그룹 목록 조회", description = "현재 등록된 캠퍼스 이슈 그룹 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<IssueGroupResponseDto>>> getAllIssueGroups() {
        List<IssueGroupResponseDto> result = issueGroupService.getAdminIssueGroups();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "이슈 그룹 상세 조회", description = "관리자 대시보드에서 특정 이슈 클릭 시 상세 정보 및 묶인 제보 원문들을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<IssueGroupDetailResponseDto>> getIssueGroupDetail(@PathVariable Long id) {
        IssueGroupDetailResponseDto detail = issueGroupService.getIssueGroupDetail(id);
        return ResponseEntity.ok(BaseResponse.success(detail));
    }
}