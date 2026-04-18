package com.example.TrainingService.exception;

public class InvalidTrainingDateRangeException extends BaseBusinessException {
    public InvalidTrainingDateRangeException(String message) {
        super(ErrorCode.INVALID_TRAINING_DATE_RANGE, message);
    }
}