package com.hrms.leaveservice.exception;

public class InvalidLeaveTypeException extends BaseBusinessException {
    public InvalidLeaveTypeException(String message) {
        super(ErrorCode.INVALID_LEAVE_TYPE, message);
    }
}