package com.hrms.reportingservice.repository;

import com.hrms.reportingservice.entity.ReportSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {

    Optional<ReportSnapshot> findByReportId(String reportId);

    Page<ReportSnapshot> findByReportIdContainingIgnoreCaseOrReportNameContainingIgnoreCaseOrGeneratedByContainingIgnoreCase(
            String reportId,
            String reportName,
            String generatedBy,
            Pageable pageable
    );
}