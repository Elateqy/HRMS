package com.hrms.leaveservice.exception;

public class RejectionReasonRequiredException extends BaseBusinessException {
    public RejectionReasonRequiredException(String message) {
        super(ErrorCode.REJECTION_REASON_REQUIRED, message);
    }
}