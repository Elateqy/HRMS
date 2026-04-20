package com.hrms.reportingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false, unique = true, length = 20)
    private String reportId;

    @Column(name = "report_name", nullable = false, length = 150)
    private String reportName;

    @Column(name = "generated_by", nullable = false, length = 100)
    private String generatedBy;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "total_employees", nullable = false)
    private long totalEmployees;

    @Column(name = "upcoming_trainings", nullable = false)
    private long upcomingTrainings;

    @Column(name = "ongoing_trainings", nullable = false)
    private long ongoingTrainings;

    @Column(name = "completed_trainings", nullable = false)
    private long completedTrainings;

    @Column(name = "cancelled_trainings", nullable = false)
    private long cancelledTrainings;

    @Column(name = "pending_leaves", nullable = false)
    private long pendingLeaves;

    @Column(name = "approved_leaves", nullable = false)
    private long approvedLeaves;

    @Column(name = "rejected_leaves", nullable = false)
    private long rejectedLeaves;

    @Column(name = "pending_enrollments", nullable = false)
    private long pendingEnrollments;

    @Column(name = "approved_enrollments", nullable = false)
    private long approvedEnrollments;

    @Column(name = "rejected_enrollments", nullable = false)
    private long rejectedEnrollments;

    @OneToMany(mappedBy = "reportSnapshot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DepartmentReportItem> departmentItems = new ArrayList<>();
}