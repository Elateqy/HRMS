package com.hrms.leaveservice.exception;

public class InactiveEmployeeException extends BaseBusinessException {
    public InactiveEmployeeException(String message) {
        super(ErrorCode.INACTIVE_EMPLOYEE, message);
    }
}