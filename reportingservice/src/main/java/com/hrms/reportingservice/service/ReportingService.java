package com.hrms.reportingservice.service;

import com.hrms.reportingservice.client.LeaveClient;
import com.hrms.reportingservice.client.TrainingClient;
import com.hrms.reportingservice.client.UserClient;
import com.hrms.reportingservice.dto.*;
import com.hrms.reportingservice.entity.*;
import com.hrms.reportingservice.exception.ExternalServiceException;
import com.hrms.reportingservice.exception.ReportGenerationException;
import com.hrms.reportingservice.exception.ReportNotFoundException;
import com.hrms.reportingservice.mapper.ReportMapper;
import com.hrms.reportingservice.repository.ReportSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final UserClient userClient;
    private final LeaveClient leaveClient;
    private final TrainingClient trainingClient;
    private final ReportSnapshotRepository reportSnapshotRepository;
    private final BusinessIdGeneratorService businessIdGeneratorService;

    public AdminDashboardSummaryResponse getAdminDashboardSummary() {
        try {
            long totalEmployees = userClient.countEmployees();

            long upcomingTrainings = trainingClient.countTrainingsByStatus(TrainingStatus.UPCOMING);
            long ongoingTrainings = trainingClient.countTrainingsByStatus(TrainingStatus.ONGOING);
            long completedTrainings = trainingClient.countTrainingsByStatus(TrainingStatus.COMPLETED);
            long cancelledTrainings = trainingClient.countTrainingsByStatus(TrainingStatus.CANCELLED);

            long pendingLeaves = leaveClient.countLeavesByStatus(LeaveStatus.PENDING);
            long approvedLeaves = leaveClient.countLeavesByStatus(LeaveStatus.APPROVED);
            long rejectedLeaves = leaveClient.countLeavesByStatus(LeaveStatus.REJECTED);

            long pendingEnrollments = trainingClient.countEnrollmentsByStatus(EnrollmentStatus.PENDING);
            long approvedEnrollments = trainingClient.countEnrollmentsByStatus(EnrollmentStatus.APPROVED);
            long rejectedEnrollments = trainingClient.countEnrollmentsByStatus(EnrollmentStatus.REJECTED);

            return AdminDashboardSummaryResponse.builder()
                    .totalEmployees(totalEmployees)
                    .upcomingTrainings(upcomingTrainings)
                    .ongoingTrainings(ongoingTrainings)
                    .completedTrainings(completedTrainings)
                    .cancelledTrainings(cancelledTrainings)
                    .pendingLeaves(pendingLeaves)
                    .approvedLeaves(approvedLeaves)
                    .rejectedLeaves(rejectedLeaves)
                    .pendingEnrollments(pendingEnrollments)
                    .approvedEnrollments(approvedEnrollments)
                    .rejectedEnrollments(rejectedEnrollments)
                    .build();

        } catch (Exception ex) {
            log.error("Failed to build admin dashboard summary", ex);
            throw new ExternalServiceException("Failed to fetch admin dashboard summary from downstream services");
        }
    }

    public List<DepartmentSummaryResponse> getDepartmentSummary() {
        try {
            List<ExternalEmployeeResponse> allEmployees = new ArrayList<>();
            int page = 0;
            int size = 500;
            boolean last = false;

            while (!last) {
                PageResponse<ExternalEmployeeResponse> response = userClient.getEmployees(page, size, "");
                if (response.getContent() != null) {
                    allEmployees.addAll(response.getContent());
                }
                last = response.isLast();
                page++;
            }

            Map<String, Long> grouped = allEmployees.stream()
                    .filter(employee -> employee.getDepartment() != null && !employee.getDepartment().isBlank())
                    .collect(Collectors.groupingBy(
                            employee -> employee.getDepartment().trim(),
                            Collectors.counting()
                    ));

            return grouped.entrySet()
                    .stream()
                    .map(entry -> DepartmentSummaryResponse.builder()
                            .department(entry.getKey())
                            .employeeCount(entry.getValue())
                            .build())
                    .sorted(Comparator.comparing(DepartmentSummaryResponse::getDepartment))
                    .toList();

        } catch (Exception ex) {
            log.error("Failed to build department summary", ex);
            throw new ExternalServiceException("Failed to fetch department summary from UserService");
        }
    }

    @Transactional
    public ReportDetailsResponse generateAndSaveReport(ReportGenerateRequest request) {
        try {
            String reportName = request.getReportName() == null ? "" : request.getReportName().trim();
            String generatedBy = request.getGeneratedBy() == null ? "" : request.getGeneratedBy().trim();

            log.info("Generating saved report reportName={} generatedBy={}", reportName, generatedBy);

            AdminDashboardSummaryResponse summary = getAdminDashboardSummary();
            List<DepartmentSummaryResponse> departments = getDepartmentSummary();

            ReportSnapshot reportSnapshot = ReportSnapshot.builder()
                    .reportId(businessIdGeneratorService.generateReportId())
                    .reportName(reportName)
                    .generatedBy(generatedBy)
                    .generatedAt(LocalDateTime.now())
                    .reportDate(LocalDate.now())
                    .totalEmployees(summary.getTotalEmployees())
                    .upcomingTrainings(summary.getUpcomingTrainings())
                    .ongoingTrainings(summary.getOngoingTrainings())
                    .completedTrainings(summary.getCompletedTrainings())
                    .cancelledTrainings(summary.getCancelledTrainings())
                    .pendingLeaves(summary.getPendingLeaves())
                    .approvedLeaves(summary.getApprovedLeaves())
                    .rejectedLeaves(summary.getRejectedLeaves())
                    .pendingEnrollments(summary.getPendingEnrollments())
                    .approvedEnrollments(summary.getApprovedEnrollments())
                    .rejectedEnrollments(summary.getRejectedEnrollments())
                    .build();

            List<DepartmentReportItem> items = departments.stream()
                    .map(department -> DepartmentReportItem.builder()
                            .department(department.getDepartment())
                            .employeeCount(department.getEmployeeCount())
                            .reportSnapshot(reportSnapshot)
                            .build())
                    .toList();

            reportSnapshot.getDepartmentItems().addAll(items);

            ReportSnapshot saved = reportSnapshotRepository.save(reportSnapshot);
            log.info("Saved report successfully reportId={}", saved.getReportId());

            return ReportMapper.toDetailsResponse(saved);
        } catch (ExternalServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to generate report", ex);
            throw new ReportGenerationException("Failed to generate report");
        }
    }

    public PageResponse<ReportHistoryResponse> getReportHistory(int page, int size, String search) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "generatedAt"));

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<ReportSnapshot> reportPage;
        if (normalizedSearch == null) {
            reportPage = reportSnapshotRepository.findAll(pageable);
        } else {
            reportPage = reportSnapshotRepository
                    .findByReportIdContainingIgnoreCaseOrReportNameContainingIgnoreCaseOrGeneratedByContainingIgnoreCase(
                            normalizedSearch,
                            normalizedSearch,
                            normalizedSearch,
                            pageable
                    );
        }

        return PageResponse.<ReportHistoryResponse>builder()
                .content(reportPage.getContent().stream().map(ReportMapper::toHistoryResponse).toList())
                .page(reportPage.getNumber())
                .size(reportPage.getSize())
                .totalElements(reportPage.getTotalElements())
                .totalPages(reportPage.getTotalPages())
                .last(reportPage.isLast())
                .build();
    }

    public ReportDetailsResponse getReportById(String reportId) {
        ReportSnapshot reportSnapshot = reportSnapshotRepository.findByReportId(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found: " + reportId));

        return ReportMapper.toDetailsResponse(reportSnapshot);
    }
}