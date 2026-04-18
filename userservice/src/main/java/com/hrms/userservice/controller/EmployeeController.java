package com.hrms.userservice.controller;

import com.hrms.userservice.dto.ApiErrorResponse;
import com.hrms.userservice.dto.EmployeeRequest;
import com.hrms.userservice.dto.EmployeeResponse;
import com.hrms.userservice.dto.PageResponse;
import com.hrms.userservice.service.EmployeeService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "APIs for employee and user management")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create employee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate employee email",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get employee by employee ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public EmployeeResponse getByEmployeeId(@PathVariable String employeeId) {
        return employeeService.getByEmployeeId(employeeId);
    }

    @GetMapping
    @Operation(summary = "Get employees with pagination, search and sort")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    public PageResponse<EmployeeResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return employeeService.getAll(page, size, search, sortBy, sortDir);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate employee email",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public EmployeeResponse updateEmployee(
            @PathVariable String employeeId,
            @Valid @RequestBody EmployeeRequest request
    ) {
        return employeeService.updateEmployee(employeeId, request);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete employee")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public void deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
    }

    @GetMapping("/count")
    @Operation(summary = "Count employees")
    @ApiResponse(responseCode = "200", description = "Employee count retrieved successfully")
    public long countEmployees() {
        return employeeService.countEmployees();
    }
}