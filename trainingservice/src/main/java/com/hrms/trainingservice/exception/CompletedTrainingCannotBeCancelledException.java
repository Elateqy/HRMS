package com.hrms.trainingservice.exception;

public class CompletedTrainingCannotBeCancelledException extends BaseBusinessException {
    public CompletedTrainingCannotBeCancelledException(String message) {
        super(ErrorCode.COMPLETED_TRAINING_CANNOT_BE_CANCELLED, message);
    }
}