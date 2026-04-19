package com.hrms.trainingservice.exception;

public class TrainingAlreadyCancelledException extends BaseBusinessException {
    public TrainingAlreadyCancelledException(String message) {
        super(ErrorCode.TRAINING_ALREADY_CANCELLED, message);
    }
}