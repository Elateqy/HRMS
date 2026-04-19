package com.hrms.leaveservice.controller;

import com.hrms.leaveservice.dto.ApiErrorResponse;
import com.hrms.leaveservice.dto.LeaveBalanceResponse;
import com.hrms.leaveservice.service.LeaveBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-balances")
@RequiredArgsConstructor
@Tag(name = "Leave Balances", description = "APIs for leave balance management")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @PostMapping("/{employeeId}/default")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create default leave balance for employee")
    public void createDefaultBalance(@PathVariable String employeeId) {
        leaveBalanceService.createDefaultBalance(employeeId);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get leave balance by employee ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Leave balance not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public LeaveBalanceResponse getByEmployeeId(@PathVariable String employeeId) {
        return leaveBalanceService.getByEmployeeId(employeeId);
    }
}