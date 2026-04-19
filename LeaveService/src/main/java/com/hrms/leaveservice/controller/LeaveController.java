package com.hrms.leaveservice.controller;

import com.hrms.leaveservice.dto.*;
import com.hrms.leaveservice.entity.LeaveStatus;
import com.hrms.leaveservice.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
@Tag(name = "Leaves", description = "APIs for leave request management")
public class LeaveController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create leave request")
    public LeaveRequestResponse createLeave(@Valid @RequestBody CreateLeaveRequest request) {
        return leaveRequestService.createLeave(request);
    }

    @GetMapping("/{leaveId}")
    @Operation(summary = "Get leave request by leave ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Leave request not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public LeaveRequestResponse getByLeaveId(@PathVariable String leaveId) {
        return leaveRequestService.getByLeaveId(leaveId);
    }

    @GetMapping
    @Operation(summary = "Get all leave requests with filters and pagination")
    public PageResponse<LeaveRequestResponse> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return leaveRequestService.getAllFiltered(status, employeeId, search, page, size);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leave requests by employee ID")
    public PageResponse<LeaveRequestResponse> getByEmployeeId(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return leaveRequestService.getByEmployeeId(employeeId, page, size);
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
        return leaveRequestService.rejectLeave(leaveId, request.getReviewedBy(), request.getRejectionReason());
    }

    @GetMapping("/count/by-status")
    @Operation(summary = "Count leave requests by status")
    public long countByStatus(@RequestParam LeaveStatus status) {
        return leaveRequestService.countByStatus(status);
    }
}