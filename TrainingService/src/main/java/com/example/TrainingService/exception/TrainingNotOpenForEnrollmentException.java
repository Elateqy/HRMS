package com.example.TrainingService.exception;

public class TrainingNotOpenForEnrollmentException extends BaseBusinessException {
    public TrainingNotOpenForEnrollmentException(String message) {
        super(ErrorCode.TRAINING_NOT_OPEN_FOR_ENROLLMENT, message);
    }
}