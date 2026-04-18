package com.example.TrainingService.exception;

public class InvalidTrainingCapacityException extends BaseBusinessException {
    public InvalidTrainingCapacityException(String message) {
        super(ErrorCode.INVALID_TRAINING_CAPACITY, message);
    }
}