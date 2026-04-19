package com.example.TrainingService.exception;

public class EnrollmentNotPendingException extends BaseBusinessException {
    public EnrollmentNotPendingException(String message) {
        super(ErrorCode.ENROLLMENT_NOT_PENDING, message);
    }
}