package com.hrms.leaveservice.exception;

public class OverlappingLeaveRequestException extends BaseBusinessException {
    public OverlappingLeaveRequestException(String message) {
        super(ErrorCode.OVERLAPPING_LEAVE_REQUEST, message);
    }
}