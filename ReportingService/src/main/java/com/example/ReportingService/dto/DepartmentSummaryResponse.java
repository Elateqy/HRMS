package com.example.ReportingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DepartmentSummaryResponse {
    private String department;
    private long employeeCount;
}