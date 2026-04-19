package com.hrms.leaveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {
    private String employeeId;
    private Integer annualRemaining;
    private Integer sickRemaining;
    private Integer casualRemaining;
}