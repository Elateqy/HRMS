package com.hrms.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalEmployeeResponse {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String department;
    private String designation;
    private Boolean active;
}