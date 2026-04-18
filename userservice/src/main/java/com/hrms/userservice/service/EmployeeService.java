package com.hrms.userservice.service;

import com.hrms.userservice.client.LeaveClient;
import com.hrms.userservice.dto.EmployeeRequest;
import com.hrms.userservice.dto.EmployeeResponse;
import com.hrms.userservice.dto.PageResponse;
import com.hrms.userservice.entity.Employee;
import com.hrms.userservice.entity.UserType;
import com.hrms.userservice.exception.DuplicateEmployeeEmailException;
import com.hrms.userservice.exception.ResourceNotFoundException;
import com.hrms.userservice.mapper.EmployeeMapper;
import com.hrms.userservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BusinessIdGeneratorService businessIdGeneratorService;
    private final LeaveClient leaveClient;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with email={}", request.getEmail());

        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmployeeEmailException("Email already exists: " + request.getEmail());
        }

        Employee employee = EmployeeMapper.toEntity(request);
        employee.setEmployeeId(businessIdGeneratorService.generateEmployeeId());
        employee.setUserType(UserType.EMPLOYEE);

        Employee savedEmployee = employeeRepository.save(employee);

        leaveClient.createDefaultBalance(savedEmployee.getEmployeeId());

        log.info("Employee created successfully with employeeId={}", savedEmployee.getEmployeeId());
        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));

        return EmployeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAll(int page, int size, String search, String sortBy, String sortDir) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        String safeSortBy = isAllowedSortField(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortBy));

        Page<Employee> employeesPage;

        if (search == null || search.isBlank()) {
            employeesPage = employeeRepository.findAll(pageable);
        } else {
            String normalizedSearch = search.trim();
            employeesPage =
                    employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(
                            normalizedSearch,
                            normalizedSearch,
                            normalizedSearch,
                            normalizedSearch,
                            pageable
                    );
        }

        return PageResponse.<EmployeeResponse>builder()
                .content(employeesPage.getContent().stream().map(EmployeeMapper::toResponse).toList())
                .page(employeesPage.getNumber())
                .size(employeesPage.getSize())
                .totalElements(employeesPage.getTotalElements())
                .totalPages(employeesPage.getTotalPages())
                .last(employeesPage.isLast())
                .build();
    }

    @Transactional
    public EmployeeResponse updateEmployee(String employeeId, EmployeeRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));

        if (employeeRepository.existsByEmailIgnoreCaseAndEmployeeIdNot(request.getEmail(), employeeId)) {
            throw new DuplicateEmployeeEmailException("Email already exists: " + request.getEmail());
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setManagerName(request.getManagerName());
        employee.setManagerEmployeeId(request.getManagerEmployeeId());
        employee.setUserType(UserType.EMPLOYEE);

        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("Employee updated successfully with employeeId={}", updatedEmployee.getEmployeeId());
        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));

        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with employeeId={}", employeeId);
    }

    @Transactional(readOnly = true)
    public long countEmployees() {
        return employeeRepository.count();
    }

    private boolean isAllowedSortField(String sortBy) {
        return "id".equalsIgnoreCase(sortBy)
                || "firstName".equalsIgnoreCase(sortBy)
                || "lastName".equalsIgnoreCase(sortBy)
                || "email".equalsIgnoreCase(sortBy)
                || "employeeId".equalsIgnoreCase(sortBy)
                || "department".equalsIgnoreCase(sortBy)
                || "designation".equalsIgnoreCase(sortBy);
    }
}