package com.sku.backend.domain.report.service;

import com.sku.backend.domain.report.dto.ReportCreateRequestDto;
import com.sku.backend.domain.report.dto.ReportLikeResponseDto;
import com.sku.backend.domain.report.dto.ReportResponseDto;
import com.sku.backend.domain.report.entity.Report;
import com.sku.backend.domain.report.entity.ReportLike;
import com.sku.backend.domain.report.entity.ReportStatus;
import com.sku.backend.domain.report.repository.ReportLikeRepository;
import com.sku.backend.domain.report.repository.ReportRepository;
import com.sku.backend.domain.user.entity.User;
import com.sku.backend.domain.user.repository.UserRepository;
import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportLikeRepository reportLikeRepository;

    // 제보 등록
    @Transactional
    public ReportResponseDto createReport(ReportCreateRequestDto requestDto) {
        User student = userRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        Report report = Report.builder()
                .student(student)
                .building(requestDto.getBuilding())
                .floor(requestDto.getFloor())
                .spaceDetail(requestDto.getSpaceDetail())
                .content(requestDto.getContent())
                .photoUrl(requestDto.getPhotoUrl())
                .build();

        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.from(savedReport);
    }

    // 내 제보 목록 보기
    public List<ReportResponseDto> getMyReports(Long studentId) {
        List<Report> reports = reportRepository.findAllByStudentIdOrderByIdDesc(studentId);
        return reports.stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());
    }

    // 제보 상세 조회
    public ReportResponseDto getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.REPORT_NOT_FOUND));
        return ReportResponseDto.from(report);
    }

    // 전체 제보 목록 조회
    public List<ReportResponseDto> getAllReports(ReportStatus status) {
        List<Report> reports;

        if (status == null) {
            reports = reportRepository.findAllByOrderByIdDesc();
        } else {
            reports = reportRepository.findAllByStatusOrderByIdDesc(status);
        }

        return reports.stream()
                .map(ReportResponseDto::from)
                .collect(Collectors.toList());
    }

    // 제보 공감 증감
    @Transactional
    public ReportLikeResponseDto toggleLike(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.REPORT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.USER_NOT_FOUND));

        Optional<ReportLike> reportLike = reportLikeRepository.findByReportAndUser(report, user);

        boolean liked;
        if (reportLike.isPresent()) {
            reportLikeRepository.delete(reportLike.get());
            report.decreaseLikeCount();
            liked = false;
        } else {
            ReportLike newLike = ReportLike.builder()
                    .report(report)
                    .user(user)
                    .build();
            reportLikeRepository.save(newLike);
            report.increaseLikeCount();
            liked = true;
        }

        return ReportLikeResponseDto.of(liked, report.getLikeCount());
    }
}