package com.hrms.leaveservice.exception;

public class LeaveNotPendingException extends BaseBusinessException {
    public LeaveNotPendingException(String message) {
        super(ErrorCode.LEAVE_NOT_PENDING, message);
    }
}