package com.example.ReportingService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "department_report_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentReportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "employee_count", nullable = false)
    private long employeeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_snapshot_id", nullable = false)
    private ReportSnapshot reportSnapshot;
}