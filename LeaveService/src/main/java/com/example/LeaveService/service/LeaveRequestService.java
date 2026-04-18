package com.example.LeaveService.service;

import com.example.LeaveService.client.UserClient;
import com.example.LeaveService.dto.CreateLeaveRequest;
import com.example.LeaveService.dto.ExternalEmployeeResponse;
import com.example.LeaveService.dto.LeaveRequestResponse;
import com.example.LeaveService.entity.LeaveRequest;
import com.example.LeaveService.entity.LeaveStatus;
import com.example.LeaveService.exception.*;
import com.example.LeaveService.mapper.LeaveMapper;
import com.example.LeaveService.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

        validateLeaveDates(request.getStartDate(), request.getEndDate());

        int durationDays = (int) ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        boolean overlapping = leaveRequestRepository.existsOverlappingRequest(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate(),
                Set.of(LeaveStatus.PENDING, LeaveStatus.APPROVED)
        );

        if (overlapping) {
            throw new OverlappingLeaveRequestException("Overlapping leave request already exists");
        }

        leaveBalanceService.validateSufficientBalance(
                request.getEmployeeId(),
                request.getLeaveType(),
                durationDays
        );

        LeaveRequest leaveRequest = LeaveMapper.toEntity(request);
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

    public Page<LeaveRequestResponse> getAllFiltered(LeaveStatus status, String employeeId, String search, Pageable pageable) {
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        String normalizedEmployeeId = (employeeId == null || employeeId.isBlank()) ? null : employeeId.trim();

        return leaveRequestRepository.searchLeaves(status, normalizedEmployeeId, normalizedSearch, pageable)
                .map(LeaveMapper::toResponse);
    }

    public Page<LeaveRequestResponse> getByEmployeeId(String employeeId, Pageable pageable) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable)
                .map(LeaveMapper::toResponse);
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

    public long countPendingRequests() {
        return leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
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