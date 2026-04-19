package com.hrms.trainingservice.exception;

public class MaxEnrollmentsExceededException extends BaseBusinessException {
    public MaxEnrollmentsExceededException(String message) {
        super(ErrorCode.MAX_ENROLLMENTS_EXCEEDED, message);
    }
}