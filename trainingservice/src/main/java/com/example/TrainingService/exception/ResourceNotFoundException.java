package com.example.TrainingService.exception;

public class ResourceNotFoundException extends BaseBusinessException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}