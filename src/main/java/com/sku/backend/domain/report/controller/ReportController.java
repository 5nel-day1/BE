package com.sku.backend.domain.report.controller;

import com.sku.backend.domain.report.dto.ReportCreateRequestDto;
import com.sku.backend.domain.report.dto.ReportLikeResponseDto;
import com.sku.backend.domain.report.dto.ReportResponseDto;
import com.sku.backend.domain.report.entity.ReportStatus;
import com.sku.backend.domain.report.service.ReportService;
import com.sku.backend.global.common.BaseResponse;
import com.sku.backend.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Report", description = "제보 API")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "제보 등록", description = "새로운 시설 불편 제보를 등록합니다. 이미지는 선택 사항(최대 1장)입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<ReportResponseDto> createReport(
            @Parameter(
                    description = "제보 작성 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "data") ReportCreateRequestDto requestDto,

            @Parameter(description = "제보 첨부 사진 (최대 1장, 선택 사항)")
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ReportResponseDto responseDto = reportService.createReport(currentUserId, requestDto, image);
        return BaseResponse.success(responseDto);
    }

    @Operation(summary = "내 제보 목록 조회", description = "로그인한 유저(학생)의 제보 목록을 조회합니다.")
    @GetMapping("/me")
    public BaseResponse<List<ReportResponseDto>> getMyReports() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ReportResponseDto> responseDtos = reportService.getMyReports(currentUserId);
        return BaseResponse.success(responseDtos);
    }

    @Operation(summary = "제보 상세 조회", description = "특정 제보의 상세 정보와 처리 상태를 조회합니다.")
    @GetMapping("/{reportId}")
    public BaseResponse<ReportResponseDto> getReportDetail(@PathVariable Long reportId) {
        ReportResponseDto responseDto = reportService.getReportDetail(reportId);
        return BaseResponse.success(responseDto);
    }

    @Operation(summary = "전체 제보 목록 조회", description = "학생 홈 화면의 전체 제보 피드를 조회합니다. status 파라미터로 필터링 가능합니다.")
    @GetMapping
    public BaseResponse<List<ReportResponseDto>> getAllReports(
            @RequestParam(required = false) ReportStatus status) {
        List<ReportResponseDto> responseDtos = reportService.getAllReports(status);
        return BaseResponse.success(responseDtos);
    }

    @Operation(summary = "제보 공감 토글", description = "특정 제보에 유저별 공감을 누르거나 취소합니다.")
    @PostMapping("/{reportId}/like")
    public BaseResponse<ReportLikeResponseDto> toggleLike(@PathVariable Long reportId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ReportLikeResponseDto responseDto = reportService.toggleLike(reportId, currentUserId);
        return BaseResponse.success(responseDto);
    }
}