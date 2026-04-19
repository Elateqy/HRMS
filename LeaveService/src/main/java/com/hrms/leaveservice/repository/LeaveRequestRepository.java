package com.hrms.leaveservice.repository;

import com.hrms.leaveservice.entity.LeaveRequest;
import com.hrms.leaveservice.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Optional<LeaveRequest> findByLeaveId(String leaveId);

    Page<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId, Pageable pageable);

    long countByStatus(LeaveStatus status);

    Page<LeaveRequest> findByStatusAndEmployeeIdContainingIgnoreCaseOrderByCreatedAtDesc(
            LeaveStatus status,
            String employeeId,
            Pageable pageable
    );

    Page<LeaveRequest> findByEmployeeIdContainingIgnoreCaseOrderByCreatedAtDesc(
            String employeeId,
            Pageable pageable
    );

    Page<LeaveRequest> findByStatusOrderByCreatedAtDesc(
            LeaveStatus status,
            Pageable pageable
    );

    Page<LeaveRequest> findByLeaveIdContainingIgnoreCaseOrEmployeeNameContainingIgnoreCaseOrReviewedByContainingIgnoreCaseOrderByCreatedAtDesc(
            String leaveId,
            String employeeName,
            String reviewedBy,
            Pageable pageable
    );

    List<LeaveRequest> findByEmployeeIdAndStatusIn(String employeeId, Set<LeaveStatus> statuses);

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String employeeId,
            Set<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );
}