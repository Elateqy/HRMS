package com.hrms.userservice.exception;

public class ManagerNotFoundException extends BaseBusinessException {
    public ManagerNotFoundException(String message) {
        super(ErrorCode.MANAGER_NOT_FOUND, message);
    }
}