package com.hrms.trainingservice.dto;

import lombok.Data;

@Data
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