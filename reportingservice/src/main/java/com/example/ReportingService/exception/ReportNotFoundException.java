package com.example.ReportingService.exception;

public class ReportNotFoundException extends BaseBusinessException {
    public ReportNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}