package com.example.LeaveService.exception;

public class InsufficientLeaveBalanceException extends BaseBusinessException {
    public InsufficientLeaveBalanceException(String message) {
        super(ErrorCode.INSUFFICIENT_LEAVE_BALANCE, message);
    }
}