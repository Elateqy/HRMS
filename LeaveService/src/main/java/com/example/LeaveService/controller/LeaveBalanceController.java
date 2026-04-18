package com.example.LeaveService.controller;

import com.example.LeaveService.dto.LeaveBalanceResponse;
import com.example.LeaveService.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @PostMapping("/{employeeId}/default")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDefaultBalance(@PathVariable String employeeId) {
        leaveBalanceService.createDefaultBalance(employeeId);
    }

    @GetMapping("/{employeeId}")
    public LeaveBalanceResponse getByEmployeeId(@PathVariable String employeeId) {
        return leaveBalanceService.getByEmployeeId(employeeId);
    }
}