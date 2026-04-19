package com.example.TrainingService.exception;

public class MaxEnrollmentsExceededException extends BaseBusinessException {
    public MaxEnrollmentsExceededException(String message) {
        super(ErrorCode.MAX_ENROLLMENTS_EXCEEDED, message);
    }
}