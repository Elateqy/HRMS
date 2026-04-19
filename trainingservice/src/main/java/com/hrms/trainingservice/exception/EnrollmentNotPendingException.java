package com.hrms.trainingservice.exception;

public class EnrollmentNotPendingException extends BaseBusinessException {
    public EnrollmentNotPendingException(String message) {
        super(ErrorCode.ENROLLMENT_NOT_PENDING, message);
    }
}