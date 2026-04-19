package com.hrms.leaveservice.exception;

public class InvalidLeaveDateRangeException extends BaseBusinessException {
    public InvalidLeaveDateRangeException(String message) {
        super(ErrorCode.INVALID_LEAVE_DATE_RANGE, message);
    }
}