package com.example.TrainingService.exception;

public class DuplicateEnrollmentException extends BaseBusinessException {
    public DuplicateEnrollmentException(String message) {
        super(ErrorCode.DUPLICATE_ENROLLMENT, message);
    }
}