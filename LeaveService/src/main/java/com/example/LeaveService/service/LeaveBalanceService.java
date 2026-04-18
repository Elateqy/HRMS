package com.example.LeaveService.service;

import com.example.LeaveService.dto.LeaveBalanceResponse;
import com.example.LeaveService.entity.LeaveBalance;
import com.example.LeaveService.entity.LeaveType;
import com.example.LeaveService.exception.InsufficientLeaveBalanceException;
import com.example.LeaveService.exception.InvalidLeaveTypeException;
import com.example.LeaveService.exception.ResourceNotFoundException;
import com.example.LeaveService.mapper.LeaveMapper;
import com.example.LeaveService.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    public void createDefaultBalance(String employeeId) {
        leaveBalanceRepository.findByEmployeeId(employeeId).ifPresentOrElse(
                balance -> log.info("Leave balance already exists for employeeId={}", employeeId),
                () -> {
                    LeaveBalance balance = LeaveBalance.builder()
                            .employeeId(employeeId)
                            .annualRemaining(21)
                            .sickRemaining(10)
                            .casualRemaining(7)
                            .build();

                    leaveBalanceRepository.save(balance);
                    log.info("Default leave balance created for employeeId={}", employeeId);
                }
        );
    }

    public LeaveBalanceResponse getByEmployeeId(String employeeId) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not found for employee: " + employeeId
                ));

        return LeaveMapper.toBalanceResponse(balance);
    }

    public void deductBalanceForApprovedLeave(String employeeId, LeaveType leaveType, int durationDays) {
        if (leaveType == LeaveType.UNPAID) {
            return;
        }

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not found for employee: " + employeeId
                ));

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
                // no deduction needed
            }
            default -> throw new InvalidLeaveTypeException("Unsupported leave type: " + leaveType);
        }

        leaveBalanceRepository.save(balance);
        log.info("Leave balance deducted employeeId={} leaveType={} durationDays={}",
                employeeId, leaveType, durationDays);
    }

    public void validateSufficientBalance(String employeeId, LeaveType leaveType, int durationDays) {
        if (leaveType == LeaveType.UNPAID) {
            return;
        }

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not found for employee: " + employeeId
                ));

        switch (leaveType) {
            case ANNUAL -> validateEnough(balance.getAnnualRemaining(), durationDays, "annual");
            case SICK -> validateEnough(balance.getSickRemaining(), durationDays, "sick");
            case CASUAL -> validateEnough(balance.getCasualRemaining(), durationDays, "casual");
            case UNPAID -> {
            }
            default -> throw new InvalidLeaveTypeException("Unsupported leave type: " + leaveType);
        }
    }

    private void validateEnough(int remaining, int requested, String type) {
        if (requested > remaining) {
            throw new InsufficientLeaveBalanceException(
                    "Insufficient " + type + " leave balance"
            );
        }
    }


}