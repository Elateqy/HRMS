package com.hrms.trainingservice.exception;

public class DuplicateResourceException extends BaseBusinessException {
    public DuplicateResourceException(String message) {
        super(ErrorCode.DUPLICATE_RESOURCE, message);
    }
}