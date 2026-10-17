package com.sku.backend.domain.issuegroup.service;

import com.sku.backend.domain.issuegroup.dto.IssueGroupDetailResponseDto;
import com.sku.backend.domain.issuegroup.dto.IssueGroupResponseDto;
import com.sku.backend.domain.issuegroup.dto.ReportTrendDto;
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
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

        IssueGroup issueGroup = issueGroupRepository
                .findByBuildingAndFacilityAndProblemTypeAndStatusNot(building, facility, problemType, "RESOLVED")
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

    // 관리자 대시보드: 최근 7일 가중치 반영 점수가 높은 순서대로 이슈 그룹 조회
    @Transactional(readOnly = true)
    public List<IssueGroupResponseDto> getAdminIssueGroups() {
        List<IssueGroup> issueGroups = issueGroupRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusDays(7);

        return issueGroups.stream()
                .map(group -> {
                    List<AiAnalysis> analyses = aiAnalysisRepository.findByIssueGroupId(group.getId());
                    if (analyses.isEmpty()) return null;

                    AiAnalysis representative = analyses.get(0);

                    long recentWeekCount = analyses.stream()
                            .filter(a -> a.getReport() != null && a.getReport().getCreatedAt() != null &&
                                    !a.getReport().getCreatedAt().toLocalDate().isBefore(oneWeekAgo))
                            .count();

                    int baseScore = representative.getUrgencyScore() != null ? representative.getUrgencyScore() : 0;
                    int calculatedUrgencyScore = (int) (baseScore + (recentWeekCount * 10));

                    return IssueGroupResponseDto.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .building(group.getBuilding())
                            .floor(representative.getFloor())
                            .facility(group.getFacility())
                            .problemType(group.getProblemType())
                            .severity(representative.getSeverity())
                            .urgencyScore(calculatedUrgencyScore)
                            .aiSummary(representative.getAiSummary())
                            .status(group.getStatus())
                            .build();
                })
                .filter(dto -> dto != null)
                .sorted(Comparator.comparingInt(IssueGroupResponseDto::getUrgencyScore).reversed())
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
                .map(a -> a.getReport() != null ? a.getReport().getContent() : "")
                .collect(Collectors.toList());

        String formattedResolvedAt = null;
        if (issueGroup.getResolvedAt() != null) {
            formattedResolvedAt = issueGroup.getResolvedAt().format(DateTimeFormatter.ofPattern("MM.dd"));
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate oneWeekAgo = today.minusDays(7);
        long recentWeekCount = analyses.stream()
                .filter(a -> a.getReport() != null && a.getReport().getCreatedAt() != null &&
                        !a.getReport().getCreatedAt().toLocalDate().isBefore(oneWeekAgo))
                .count();

        int baseScore = representative != null && representative.getUrgencyScore() != null ? representative.getUrgencyScore() : 0;
        int calculatedUrgencyScore = (int) (baseScore + (recentWeekCount * 10));

        List<LocalDate> targetDates = List.of(
                today.minusDays(2),
                today.minusDays(1),
                today
        );

        Map<LocalDate, Long> reportCountMap = analyses.stream()
                .map(a -> a.getReport() != null && a.getReport().getCreatedAt() != null ? a.getReport().getCreatedAt().toLocalDate() : null)
                .filter(date -> date != null)
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()));

        List<ReportTrendDto> reportTrend = targetDates.stream()
                .map(date -> ReportTrendDto.builder()
                        .date(date.format(formatter))
                        .count(reportCountMap.getOrDefault(date, 0L))
                        .build())
                .collect(Collectors.toList());

        return IssueGroupDetailResponseDto.builder()
                .id(issueGroup.getId())
                .title(issueGroup.getTitle())
                .building(issueGroup.getBuilding())
                .floor(representative != null ? representative.getFloor() : "")
                .facility(issueGroup.getFacility())
                .problemType(issueGroup.getProblemType())
                .severity(representative != null ? representative.getSeverity() : "보통")
                .urgencyScore(calculatedUrgencyScore)
                .aiSummary(representative != null ? representative.getAiSummary() : "")
                .status(issueGroup.getStatus())
                .reportContents(reportContents)
                .resolutionContent(issueGroup.getResolutionContent())
                .resolvedAt(formattedResolvedAt)
                .reportTrend(reportTrend)
                .build();
    }

    // 관리자 이슈 처리 상태 및 결과 변경
    @Transactional
    public IssueGroupDetailResponseDto updateIssueGroupStatus(Long issueGroupId, String newStatus, String resolutionContent) {
        IssueGroup issueGroup = issueGroupRepository.findById(issueGroupId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.ISSUE_GROUP_NOT_FOUND));

        issueGroup.updateStatus(newStatus);

        LocalDateTime now = LocalDateTime.now();

        if ("RESOLVED".equals(newStatus) && StringUtils.hasText(resolutionContent)) {
            issueGroup.updateResolution(resolutionContent, now);
        }

        List<AiAnalysis> analyses = aiAnalysisRepository.findByIssueGroupId(issueGroupId);
        ReportStatus reportStatus = ReportStatus.valueOf(newStatus);

        for (AiAnalysis analysis : analyses) {
            Report report = analysis.getReport();
            if (report != null) {
                report.updateStatus(reportStatus);
                if ("RESOLVED".equals(newStatus) && StringUtils.hasText(resolutionContent)) {
                    report.updateResolution(resolutionContent, now);
                }
            }
        }
        return getIssueGroupDetail(issueGroupId);
    }
}