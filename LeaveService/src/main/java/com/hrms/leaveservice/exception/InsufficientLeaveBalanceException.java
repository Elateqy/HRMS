package com.hrms.leaveservice.exception;

public class InsufficientLeaveBalanceException extends BaseBusinessException {
    public InsufficientLeaveBalanceException(String message) {
        super(ErrorCode.INSUFFICIENT_LEAVE_BALANCE, message);
    }
}