package com.hrms.reportingservice.exception;

public class ExternalServiceException extends BaseBusinessException {
    public ExternalServiceException(String message) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, message);
    }
}