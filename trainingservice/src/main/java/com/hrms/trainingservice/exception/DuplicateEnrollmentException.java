package com.hrms.trainingservice.exception;

public class DuplicateEnrollmentException extends BaseBusinessException {
    public DuplicateEnrollmentException(String message) {
        super(ErrorCode.DUPLICATE_ENROLLMENT, message);
    }
}