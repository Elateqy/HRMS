package com.hrms.trainingservice.exception;

public class InvalidAllowedDepartmentsException extends BaseBusinessException {
    public InvalidAllowedDepartmentsException(String message) {
        super(ErrorCode.INVALID_ALLOWED_DEPARTMENTS, message);
    }
}