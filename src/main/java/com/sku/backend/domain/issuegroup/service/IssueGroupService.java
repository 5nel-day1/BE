package com.sku.backend.domain.issuegroup.service;

import com.sku.backend.domain.issuegroup.dto.IssueGroupDetailResponseDto;
import com.sku.backend.domain.issuegroup.dto.IssueGroupResponseDto;
import com.sku.backend.domain.issuegroup.entity.AiAnalysis;
import com.sku.backend.domain.issuegroup.entity.IssueGroup;
import com.sku.backend.domain.issuegroup.repository.AiAnalysisRepository;
import com.sku.backend.domain.issuegroup.repository.IssueGroupRepository;
import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.entity.ReportStatus;
import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueGroupService {

    private final OpenAiService openAiService;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final IssueGroupRepository issueGroupRepository;

    // AI 분석 수행 및 기존 유사 문제군 연결 또는 신규 생성
    @Transactional
    public void processReportAiAnalysis(Report report) {
        String buildingCode = report.getBuilding() != null ? report.getBuilding().name() : "";
        String floorCode = report.getFloor() != null ? report.getFloor().name() : "";
        String spaceDetail = report.getSpaceDetail() != null ? report.getSpaceDetail() : "";

        Map<String, Object> aiResult = openAiService.analyzeReportText(buildingCode, floorCode, spaceDetail, report.getContent());
        if (aiResult == null) return;

        String building = (String) aiResult.get("building");
        String floor = (String) aiResult.get("floor");
        String facility = (String) aiResult.get("facility");
        String problemType = (String) aiResult.get("problemType");
        String summaryTitle = (String) aiResult.get("summaryTitle");

        // 유사 제보 묶기
        IssueGroup issueGroup = issueGroupRepository
                .findByBuildingAndFacilityAndProblemType(building, facility, problemType)
                .orElseGet(() -> {
                    IssueGroup newGroup = IssueGroup.builder()
                            .title(summaryTitle)
                            .building(building)
                            .facility(facility)
                            .problemType(problemType)
                            .status("RECEIVED")
                            .build();
                    return issueGroupRepository.save(newGroup);
                });

        AiAnalysis aiAnalysis = AiAnalysis.builder()
                .report(report)
                .issueGroup(issueGroup)
                .building(building)
                .floor(floor)
                .facility(facility)
                .problemType(problemType)
                .severity((String) aiResult.get("severity"))
                .summaryTitle(summaryTitle)
                .aiSummary((String) aiResult.get("aiSummary"))
                .urgencyScore((Integer) aiResult.get("urgencyScore"))
                .build();

        aiAnalysisRepository.save(aiAnalysis);
    }

    // 관리자 대시보드: 점검 필요도가 높은 순서대로 조회
    @Transactional(readOnly = true)
    public List<IssueGroupResponseDto> getAdminIssueGroups() {
        List<AiAnalysis> analyses = aiAnalysisRepository.findAllByOrderByUrgencyScoreDesc();

        return analyses.stream()
                .map(analysis -> IssueGroupResponseDto.builder()
                        .id(analysis.getId())
                        .title(analysis.getSummaryTitle())
                        .building(analysis.getBuilding())
                        .floor(analysis.getFloor())
                        .facility(analysis.getFacility())
                        .problemType(analysis.getProblemType())
                        .severity(analysis.getSeverity())
                        .urgencyScore(analysis.getUrgencyScore())
                        .aiSummary(analysis.getAiSummary())
                        .status(analysis.getReport().getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    // 관리자 이슈 상세 조회
    @Transactional(readOnly = true)
    public IssueGroupDetailResponseDto getIssueGroupDetail(Long issueGroupId) {
        IssueGroup issueGroup = issueGroupRepository.findById(issueGroupId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.ISSUE_GROUP_NOT_FOUND));

        List<AiAnalysis> analyses = aiAnalysisRepository.findByIssueGroupId(issueGroupId);

        AiAnalysis representative = analyses.isEmpty() ? null : analyses.get(0);

        List<String> reportContents = analyses.stream()
                .map(a -> a.getReport().getContent())
                .collect(Collectors.toList());

        return IssueGroupDetailResponseDto.builder()
                .id(issueGroup.getId())
                .title(issueGroup.getTitle())
                .building(issueGroup.getBuilding())
                .floor(representative != null ? representative.getFloor() : "")
                .facility(issueGroup.getFacility())
                .problemType(issueGroup.getProblemType())
                .severity(representative != null ? representative.getSeverity() : "보통")
                .urgencyScore(representative != null ? representative.getUrgencyScore() : 0)
                .aiSummary(representative != null ? representative.getAiSummary() : "")
                .status(issueGroup.getStatus())
                .reportContents(reportContents)
                .build();
    }

    // 관리자 이슈 처리 상태 변경 (접수, 확인 중, 처리 중, 해결 완료)
// IssueGroupService.java
    @Transactional
    public void updateIssueGroupStatus(Long issueGroupId, String newStatus) {
        IssueGroup issueGroup = issueGroupRepository.findById(issueGroupId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.ISSUE_GROUP_NOT_FOUND));

        issueGroup.updateStatus(newStatus);

        List<AiAnalysis> analyses = aiAnalysisRepository.findByIssueGroupId(issueGroupId);
        ReportStatus reportStatus = ReportStatus.valueOf(newStatus);

        for (AiAnalysis analysis : analyses) {
            Report report = analysis.getReport();
            if (report != null) {
                report.updateStatus(reportStatus);
            }
        }
    }
}