package com.example.TrainingService.exception;

public class TrainingAlreadyCancelledException extends BaseBusinessException {
    public TrainingAlreadyCancelledException(String message) {
        super(ErrorCode.TRAINING_ALREADY_CANCELLED, message);
    }
}