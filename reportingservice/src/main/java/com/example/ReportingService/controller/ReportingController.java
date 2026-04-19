package com.example.ReportingService.controller;

import com.example.ReportingService.dto.*;
import com.example.ReportingService.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting Controller", description = "APIs for dashboard and reports")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/admin-dashboard")
    @Operation(summary = "Get admin dashboard summary")
    public AdminDashboardSummaryResponse getAdminDashboardSummary() {
        return reportingService.getAdminDashboardSummary();
    }

    @GetMapping("/departments")
    @Operation(summary = "Get department summary")
    public List<DepartmentSummaryResponse> getDepartmentSummary() {
        return reportingService.getDepartmentSummary();
    }

    @PostMapping
    @Operation(summary = "Generate and save report")
    public ReportDetailsResponse generateAndSaveReport(@Valid @RequestBody ReportGenerateRequest request) {
        return reportingService.generateAndSaveReport(request);
    }

    @GetMapping
    @Operation(summary = "Get report history")
    public PageResponse<ReportHistoryResponse> getReportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        return reportingService.getReportHistory(page, size, search);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Get report by report ID")
    public ReportDetailsResponse getReportById(@PathVariable String reportId) {
        return reportingService.getReportById(reportId);
    }
}