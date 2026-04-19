package com.hrms.leaveservice.service;

import com.hrms.leaveservice.client.UserClient;
import com.hrms.leaveservice.dto.CreateLeaveRequest;
import com.hrms.leaveservice.dto.ExternalEmployeeResponse;
import com.hrms.leaveservice.dto.LeaveRequestResponse;
import com.hrms.leaveservice.dto.PageResponse;
import com.hrms.leaveservice.entity.LeaveRequest;
import com.hrms.leaveservice.entity.LeaveStatus;
import com.hrms.leaveservice.exception.*;
import com.hrms.leaveservice.mapper.LeaveMapper;
import com.hrms.leaveservice.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final BusinessIdGeneratorService businessIdGeneratorService;
    private final UserClient userClient;

    public LeaveRequestResponse createLeave(CreateLeaveRequest request) {
        log.info("Creating leave request for employeeId={}", request.getEmployeeId());

        ExternalEmployeeResponse employee = userClient.getEmployeeById(request.getEmployeeId());
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found: " + request.getEmployeeId());
        }

        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new InactiveEmployeeException("Employee is inactive: " + request.getEmployeeId());
        }

        validateLeaveDates(request.getStartDate(), request.getEndDate());

        int durationDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        boolean overlapping = leaveRequestRepository
                .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        request.getEmployeeId(),
                        Set.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                        request.getEndDate(),
                        request.getStartDate()
                );

        if (overlapping) {
            throw new OverlappingLeaveRequestException("Overlapping leave request already exists");
        }

        leaveBalanceService.validateSufficientBalance(request.getEmployeeId(), request.getLeaveType(), durationDays);

        LeaveRequest leaveRequest = LeaveMapper.toEntity(request, employee);
        leaveRequest.setLeaveId(businessIdGeneratorService.generateLeaveId());
        leaveRequest.setDurationDays(durationDays);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setReviewedAt(null);
        leaveRequest.setReviewedBy(null);
        leaveRequest.setRejectionReason(null);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        log.info("Leave request created successfully leaveId={}", saved.getLeaveId());
        return LeaveMapper.toResponse(saved);
    }

    public LeaveRequestResponse getByLeaveId(String leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByLeaveId(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));

        return LeaveMapper.toResponse(leaveRequest);
    }

    public PageResponse<LeaveRequestResponse> getAllFiltered(LeaveStatus status, String employeeId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));

        String normalizedEmployeeId = employeeId == null || employeeId.isBlank() ? null : employeeId.trim();
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();

        Page<LeaveRequest> resultPage;

        if (status != null && normalizedEmployeeId != null) {
            resultPage = leaveRequestRepository.findByStatusAndEmployeeIdContainingIgnoreCaseOrderByCreatedAtDesc(
                    status,
                    normalizedEmployeeId,
                    pageable
            );
        } else if (status != null) {
            resultPage = leaveRequestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (normalizedEmployeeId != null) {
            resultPage = leaveRequestRepository.findByEmployeeIdContainingIgnoreCaseOrderByCreatedAtDesc(
                    normalizedEmployeeId,
                    pageable
            );
        } else if (normalizedSearch != null) {
            resultPage = leaveRequestRepository.findByLeaveIdContainingIgnoreCaseOrEmployeeNameContainingIgnoreCaseOrReviewedByContainingIgnoreCaseOrderByCreatedAtDesc(
                    normalizedSearch,
                    normalizedSearch,
                    normalizedSearch,
                    pageable
            );
        } else {
            resultPage = leaveRequestRepository.findAll(pageable);
        }

        return PageResponse.<LeaveRequestResponse>builder()
                .content(resultPage.getContent().stream().map(LeaveMapper::toResponse).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    public PageResponse<LeaveRequestResponse> getByEmployeeId(String employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));

        Page<LeaveRequest> resultPage = leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable);

        return PageResponse.<LeaveRequestResponse>builder()
                .content(resultPage.getContent().stream().map(LeaveMapper::toResponse).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    public LeaveRequestResponse approveLeave(String leaveId, String reviewedBy) {
        log.info("Approving leave request leaveId={}", leaveId);

        LeaveRequest leaveRequest = leaveRequestRepository.findByLeaveId(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveNotPendingException("Leave request is not pending");
        }

        leaveBalanceService.validateSufficientBalance(
                leaveRequest.getEmployeeId(),
                leaveRequest.getLeaveType(),
                leaveRequest.getDurationDays()
        );

        leaveBalanceService.deductBalanceForApprovedLeave(
                leaveRequest.getEmployeeId(),
                leaveRequest.getLeaveType(),
                leaveRequest.getDurationDays()
        );

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setReviewedBy(reviewedBy);
        leaveRequest.setRejectionReason(null);

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        log.info("Leave request approved leaveId={}", leaveId);
        return LeaveMapper.toResponse(updated);
    }

    public LeaveRequestResponse rejectLeave(String leaveId, String reviewedBy, String rejectionReason) {
        log.info("Rejecting leave request leaveId={}", leaveId);

        LeaveRequest leaveRequest = leaveRequestRepository.findByLeaveId(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveNotPendingException("Leave request is not pending");
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new RejectionReasonRequiredException("Rejection reason is required");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setReviewedBy(reviewedBy);
        leaveRequest.setRejectionReason(rejectionReason);

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        log.info("Leave request rejected leaveId={}", leaveId);
        return LeaveMapper.toResponse(updated);
    }

    public long countByStatus(LeaveStatus status) {
        return leaveRequestRepository.countByStatus(status);
    }

    private void validateLeaveDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidLeaveDateRangeException("End date must be after or equal to start date");
        }
    }
}