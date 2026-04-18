package com.example.LeaveService.mapper;

import com.example.LeaveService.dto.CreateLeaveRequest;
import com.example.LeaveService.dto.LeaveBalanceResponse;
import com.example.LeaveService.dto.LeaveRequestResponse;
import com.example.LeaveService.entity.LeaveBalance;
import com.example.LeaveService.entity.LeaveRequest;

public class LeaveMapper {

    private LeaveMapper() {
    }

    public static LeaveRequest toEntity(CreateLeaveRequest request) {
        return LeaveRequest.builder()
                .employeeId(request.getEmployeeId())
                .employeeName(request.getEmployeeName())
                .department(request.getDepartment())
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .build();
    }

    public static LeaveRequestResponse toResponse(LeaveRequest leaveRequest) {
        return LeaveRequestResponse.builder()
                .leaveId(leaveRequest.getLeaveId())
                .employeeId(leaveRequest.getEmployeeId())
                .employeeName(leaveRequest.getEmployeeName())
                .department(leaveRequest.getDepartment())
                .leaveType(leaveRequest.getLeaveType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .durationDays(leaveRequest.getDurationDays())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .createdAt(leaveRequest.getCreatedAt())
                .reviewedAt(leaveRequest.getReviewedAt())
                .reviewedBy(leaveRequest.getReviewedBy())
                .rejectionReason(leaveRequest.getRejectionReason())
                .build();
    }

    public static LeaveBalanceResponse toBalanceResponse(LeaveBalance balance) {
        return LeaveBalanceResponse.builder()
                .employeeId(balance.getEmployeeId())
                .annualRemaining(balance.getAnnualRemaining())
                .sickRemaining(balance.getSickRemaining())
                .casualRemaining(balance.getCasualRemaining())
                .build();
    }
}