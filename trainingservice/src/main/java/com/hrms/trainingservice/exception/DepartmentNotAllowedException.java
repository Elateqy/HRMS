package com.hrms.trainingservice.exception;

public class DepartmentNotAllowedException extends BaseBusinessException {
    public DepartmentNotAllowedException(String message) {
        super(ErrorCode.DEPARTMENT_NOT_ALLOWED, message);
    }
}