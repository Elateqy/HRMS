package com.hrms.userservice.mapper;

import com.hrms.userservice.dto.EmployeeRequest;
import com.hrms.userservice.dto.EmployeeResponse;
import com.hrms.userservice.entity.Employee;

public class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .managerName(request.getManagerName())
                .managerEmployeeId(request.getManagerEmployeeId())
                .build();
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .salary(employee.getSalary())
                .managerName(employee.getManagerName())
                .managerEmployeeId(employee.getManagerEmployeeId())
                .userType(employee.getUserType())
                .active(employee.getActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}