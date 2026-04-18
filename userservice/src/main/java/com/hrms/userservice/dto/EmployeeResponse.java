package com.hrms.userservice.dto;

import com.hrms.userservice.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String department;
    private String designation;
    private BigDecimal salary;
    private String managerName;
    private String managerEmployeeId;
    private UserType userType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}