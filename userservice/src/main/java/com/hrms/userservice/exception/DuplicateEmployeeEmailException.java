package com.hrms.userservice.exception;

public class DuplicateEmployeeEmailException extends BaseBusinessException {
    public DuplicateEmployeeEmailException(String message) {
        super(ErrorCode.DUPLICATE_EMPLOYEE_EMAIL, message);
    }
}