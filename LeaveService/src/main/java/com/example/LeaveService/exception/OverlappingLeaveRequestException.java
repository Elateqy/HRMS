package com.example.LeaveService.exception;

public class OverlappingLeaveRequestException extends BaseBusinessException {
    public OverlappingLeaveRequestException(String message) {
        super(ErrorCode.OVERLAPPING_LEAVE_REQUEST, message);
    }
}