package com.example.TrainingService.exception;

public class TrainingCapacityLessThanApprovedException extends BaseBusinessException {
    public TrainingCapacityLessThanApprovedException(String message) {
        super(ErrorCode.TRAINING_CAPACITY_LESS_THAN_APPROVED, message);
    }
}