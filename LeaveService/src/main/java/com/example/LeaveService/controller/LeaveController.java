package com.example.LeaveService.controller;

import com.example.LeaveService.dto.CreateLeaveRequest;
import com.example.LeaveService.dto.LeaveRequestResponse;
import com.example.LeaveService.dto.LeaveReviewRequest;
import com.example.LeaveService.entity.LeaveStatus;
import com.example.LeaveService.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Controller", description = "APIs for leave management")
public class LeaveController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    @Operation(summary = "Create leave request")
    public LeaveRequestResponse createLeave(@Valid @RequestBody CreateLeaveRequest request) {
        return leaveRequestService.createLeave(request);
    }

    @GetMapping("/{leaveId}")
    @Operation(summary = "Get leave request by leave ID")
    public LeaveRequestResponse getByLeaveId(@PathVariable String leaveId) {
        return leaveRequestService.getByLeaveId(leaveId);
    }

    @GetMapping
    @Operation(summary = "Get all leave requests with filters and pagination")
    public Page<LeaveRequestResponse> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) String employeeId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return leaveRequestService.getAllFiltered(status, employeeId, search, pageable);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leave requests by employee ID")
    public Page<LeaveRequestResponse> getByEmployeeId(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return leaveRequestService.getByEmployeeId(employeeId, pageable);
    }

    @PutMapping("/{leaveId}/approve")
    @Operation(summary = "Approve leave request")
    public LeaveRequestResponse approveLeave(
            @PathVariable String leaveId,
            @Valid @RequestBody LeaveReviewRequest request
    ) {
        return leaveRequestService.approveLeave(leaveId, request.getReviewedBy());
    }

    @PutMapping("/{leaveId}/reject")
    @Operation(summary = "Reject leave request")
    public LeaveRequestResponse rejectLeave(
            @PathVariable String leaveId,
            @Valid @RequestBody LeaveReviewRequest request
    ) {
        return leaveRequestService.rejectLeave(
                leaveId,
                request.getReviewedBy(),
                request.getRejectionReason()
        );
    }

    @GetMapping("/count/by-status")
    @Operation(summary = "Count leave requests by status")
    public long countByStatus(@RequestParam LeaveStatus status) {
        return leaveRequestService.countByStatus(status);
    }
}