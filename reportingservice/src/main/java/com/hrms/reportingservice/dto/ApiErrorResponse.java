package com.hrms.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
}