package com.hrms.leaveservice.mapper;

import com.hrms.leaveservice.dto.CreateLeaveRequest;
import com.hrms.leaveservice.dto.ExternalEmployeeResponse;
import com.hrms.leaveservice.dto.LeaveBalanceResponse;
import com.hrms.leaveservice.dto.LeaveRequestResponse;
import com.hrms.leaveservice.entity.LeaveBalance;
import com.hrms.leaveservice.entity.LeaveRequest;

public class LeaveMapper {

    private LeaveMapper() {
    }

    public static LeaveRequest toEntity(CreateLeaveRequest request, ExternalEmployeeResponse employee) {
        return LeaveRequest.builder()
                .employeeId(request.getEmployeeId())
                .employeeName(employee.getFullName() != null ? employee.getFullName() : employee.getFirstName() + " " + employee.getLastName())
                .department(employee.getDepartment())
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