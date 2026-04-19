package com.hrms.trainingservice.exception;

public class TrainingCapacityExceededException extends BaseBusinessException {
    public TrainingCapacityExceededException(String message) {
        super(ErrorCode.TRAINING_CAPACITY_EXCEEDED, message);
    }
}