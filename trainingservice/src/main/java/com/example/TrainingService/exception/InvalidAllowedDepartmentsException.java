package com.example.TrainingService.exception;

public class InvalidAllowedDepartmentsException extends BaseBusinessException {
    public InvalidAllowedDepartmentsException(String message) {
        super(ErrorCode.INVALID_ALLOWED_DEPARTMENTS, message);
    }
}