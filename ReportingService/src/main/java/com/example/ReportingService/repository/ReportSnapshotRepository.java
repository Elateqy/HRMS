package com.example.ReportingService.repository;

import com.example.ReportingService.entity.ReportSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, Long> {

    Optional<ReportSnapshot> findByReportId(String reportId);

    @Query("""
            SELECT r
            FROM ReportSnapshot r
            WHERE :search IS NULL OR :search = ''
               OR LOWER(r.reportId) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(r.reportName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(r.generatedBy) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<ReportSnapshot> searchReports(@Param("search") String search, Pageable pageable);
}