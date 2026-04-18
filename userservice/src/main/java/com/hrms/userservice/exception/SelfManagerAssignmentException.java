package com.hrms.userservice.exception;

public class SelfManagerAssignmentException extends BaseBusinessException {
    public SelfManagerAssignmentException(String message) {
        super(ErrorCode.SELF_MANAGER_ASSIGNMENT, message);
    }
}