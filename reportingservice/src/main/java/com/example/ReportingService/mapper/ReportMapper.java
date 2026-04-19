package com.example.ReportingService.mapper;

import com.example.ReportingService.dto.AdminDashboardSummaryResponse;
import com.example.ReportingService.dto.DepartmentSummaryResponse;
import com.example.ReportingService.dto.ReportDetailsResponse;
import com.example.ReportingService.dto.ReportHistoryResponse;
import com.example.ReportingService.entity.DepartmentReportItem;
import com.example.ReportingService.entity.ReportSnapshot;

import java.util.List;

public class ReportMapper {

    private ReportMapper() {
    }

    public static ReportHistoryResponse toHistoryResponse(ReportSnapshot reportSnapshot) {
        return ReportHistoryResponse.builder()
                .reportId(reportSnapshot.getReportId())
                .reportName(reportSnapshot.getReportName())
                .generatedBy(reportSnapshot.getGeneratedBy())
                .generatedAt(reportSnapshot.getGeneratedAt())
                .reportDate(reportSnapshot.getReportDate())
                .build();
    }

    public static ReportDetailsResponse toDetailsResponse(ReportSnapshot reportSnapshot) {
        AdminDashboardSummaryResponse summary = AdminDashboardSummaryResponse.builder()
                .totalEmployees(reportSnapshot.getTotalEmployees())
                .upcomingTrainings(reportSnapshot.getUpcomingTrainings())
                .ongoingTrainings(reportSnapshot.getOngoingTrainings())
                .completedTrainings(reportSnapshot.getCompletedTrainings())
                .cancelledTrainings(reportSnapshot.getCancelledTrainings())
                .pendingLeaves(reportSnapshot.getPendingLeaves())
                .approvedLeaves(reportSnapshot.getApprovedLeaves())
                .rejectedLeaves(reportSnapshot.getRejectedLeaves())
                .pendingEnrollments(reportSnapshot.getPendingEnrollments())
                .approvedEnrollments(reportSnapshot.getApprovedEnrollments())
                .rejectedEnrollments(reportSnapshot.getRejectedEnrollments())
                .build();

        List<DepartmentSummaryResponse> departments = reportSnapshot.getDepartmentItems()
                .stream()
                .map(ReportMapper::toDepartmentSummary)
                .toList();

        return ReportDetailsResponse.builder()
                .reportId(reportSnapshot.getReportId())
                .reportName(reportSnapshot.getReportName())
                .generatedBy(reportSnapshot.getGeneratedBy())
                .generatedAt(reportSnapshot.getGeneratedAt())
                .reportDate(reportSnapshot.getReportDate())
                .summary(summary)
                .departments(departments)
                .build();
    }

    private static DepartmentSummaryResponse toDepartmentSummary(DepartmentReportItem item) {
        return DepartmentSummaryResponse.builder()
                .department(item.getDepartment())
                .employeeCount(item.getEmployeeCount())
                .build();
    }
}