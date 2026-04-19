package com.hrms.leaveservice.service;

import com.hrms.leaveservice.dto.LeaveBalanceResponse;
import com.hrms.leaveservice.entity.LeaveBalance;
import com.hrms.leaveservice.entity.LeaveType;
import com.hrms.leaveservice.exception.InsufficientLeaveBalanceException;
import com.hrms.leaveservice.exception.InvalidLeaveTypeException;
import com.hrms.leaveservice.exception.ResourceNotFoundException;
import com.hrms.leaveservice.mapper.LeaveMapper;
import com.hrms.leaveservice.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    public void createDefaultBalance(String employeeId) {
        if (leaveBalanceRepository.existsByEmployeeId(employeeId)) {
            log.info("Leave balance already exists for employeeId={}", employeeId);
            return;
        }

        LeaveBalance balance = LeaveBalance.builder()
                .employeeId(employeeId)
                .annualRemaining(21)
                .sickRemaining(10)
                .casualRemaining(7)
                .build();

        leaveBalanceRepository.save(balance);
        log.info("Default leave balance created for employeeId={}", employeeId);
    }

    public LeaveBalanceResponse getByEmployeeId(String employeeId) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee: " + employeeId));

        return LeaveMapper.toBalanceResponse(balance);
    }

    public void validateSufficientBalance(String employeeId, LeaveType leaveType, int durationDays) {
        if (leaveType == LeaveType.UNPAID) {
            return;
        }

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee: " + employeeId));

        switch (leaveType) {
            case ANNUAL -> validateEnough(balance.getAnnualRemaining(), durationDays, "annual");
            case SICK -> validateEnough(balance.getSickRemaining(), durationDays, "sick");
            case CASUAL -> validateEnough(balance.getCasualRemaining(), durationDays, "casual");
            case UNPAID -> {
            }
            default -> throw new InvalidLeaveTypeException("Unsupported leave type: " + leaveType);
        }
    }

    public void deductBalanceForApprovedLeave(String employeeId, LeaveType leaveType, int durationDays) {
        if (leaveType == LeaveType.UNPAID) {
            return;
        }

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee: " + employeeId));

        switch (leaveType) {
            case ANNUAL -> {
                validateEnough(balance.getAnnualRemaining(), durationDays, "annual");
                balance.setAnnualRemaining(balance.getAnnualRemaining() - durationDays);
            }
            case SICK -> {
                validateEnough(balance.getSickRemaining(), durationDays, "sick");
                balance.setSickRemaining(balance.getSickRemaining() - durationDays);
            }
            case CASUAL -> {
                validateEnough(balance.getCasualRemaining(), durationDays, "casual");
                balance.setCasualRemaining(balance.getCasualRemaining() - durationDays);
            }
            case UNPAID -> {
            }
            default -> throw new InvalidLeaveTypeException("Unsupported leave type: " + leaveType);
        }

        leaveBalanceRepository.save(balance);
        log.info("Leave balance deducted for employeeId={}, leaveType={}, days={}", employeeId, leaveType, durationDays);
    }

    private void validateEnough(int remaining, int requested, String type) {
        if (requested > remaining) {
            throw new InsufficientLeaveBalanceException("Insufficient " + type + " leave balance");
        }
    }
}