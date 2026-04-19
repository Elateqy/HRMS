package com.example.ReportingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AdminDashboardSummaryResponse {
    private long totalEmployees;
    private long upcomingTrainings;
    private long ongoingTrainings;
    private long completedTrainings;
    private long cancelledTrainings;

    private long pendingLeaves;
    private long approvedLeaves;
    private long rejectedLeaves;

    private long pendingEnrollments;
    private long approvedEnrollments;
    private long rejectedEnrollments;
}