package com.example.ReportingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ReportDetailsResponse {
    private String reportId;
    private String reportName;
    private String generatedBy;
    private LocalDateTime generatedAt;
    private LocalDate reportDate;
    private AdminDashboardSummaryResponse summary;
    private List<DepartmentSummaryResponse> departments;
}