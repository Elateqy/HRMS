package com.example.TrainingService.exception;

public class DepartmentNotAllowedException extends BaseBusinessException {
    public DepartmentNotAllowedException(String message) {
        super(ErrorCode.DEPARTMENT_NOT_ALLOWED, message);
    }
}