package com.example.TrainingService.dto;

import com.example.TrainingService.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private String enrollmentId;
    private String trainingId;
    private String trainingTitle;
    private String employeeId;
    private String employeeName;
    private String department;
    private EnrollmentStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String rejectionReason;
}