package com.example.LeaveService.repository;

import com.example.LeaveService.entity.LeaveRequest;
import com.example.LeaveService.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Optional<LeaveRequest> findByLeaveId(String leaveId);

    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    Page<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId, Pageable pageable);

    long countByStatus(LeaveStatus status);

    @Query("""
            SELECT l
            FROM LeaveRequest l
            WHERE (:status IS NULL OR l.status = :status)
              AND (:employeeId IS NULL OR :employeeId = '' OR LOWER(l.employeeId) LIKE LOWER(CONCAT('%', :employeeId, '%')))
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(l.leaveId) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(l.employeeName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(l.reviewedBy, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY l.createdAt DESC
            """)
    Page<LeaveRequest> searchLeaves(
            @Param("status") LeaveStatus status,
            @Param("employeeId") String employeeId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LeaveRequest l
            WHERE l.employeeId = :employeeId
              AND l.status IN :statuses
              AND (
                    (:startDate BETWEEN l.startDate AND l.endDate)
                    OR (:endDate BETWEEN l.startDate AND l.endDate)
                    OR (l.startDate BETWEEN :startDate AND :endDate)
                  )
            """)
    boolean existsOverlappingRequest(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<LeaveStatus> statuses
    );
}