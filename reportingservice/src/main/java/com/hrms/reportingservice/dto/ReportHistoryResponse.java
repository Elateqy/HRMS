package com.hrms.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReportHistoryResponse {
    private String reportId;
    private String reportName;
    private String generatedBy;
    private LocalDateTime generatedAt;
    private LocalDate reportDate;
}