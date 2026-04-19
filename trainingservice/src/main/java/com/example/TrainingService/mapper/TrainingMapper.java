package com.example.TrainingService.mapper;

import com.example.TrainingService.dto.EnrollmentResponse;
import com.example.TrainingService.dto.TrainingCreateRequest;
import com.example.TrainingService.dto.TrainingEnrollmentResponse;
import com.example.TrainingService.dto.TrainingResponse;
import com.example.TrainingService.entity.Enrollment;
import com.example.TrainingService.entity.Training;

public class TrainingMapper {

    private TrainingMapper() {
    }

    public static Training toEntity(TrainingCreateRequest request) {
        return Training.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .capacity(request.getCapacity())
                .allowedDepartments(request.getAllowedDepartments())
                .createdBy(request.getCreatedBy())
                .build();
    }

    public static TrainingResponse toResponse(Training training) {
        return TrainingResponse.builder()
                .trainingId(training.getTrainingId())
                .title(training.getTitle())
                .description(training.getDescription())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .durationDays(training.getDurationDays())
                .capacity(training.getCapacity())
                .allowedDepartments(training.getAllowedDepartments())
                .status(training.getStatus())
                .createdBy(training.getCreatedBy())
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }

    public static TrainingEnrollmentResponse toEnrollmentTrainingResponse(Training training) {
        return TrainingEnrollmentResponse.builder()
                .trainingId(training.getTrainingId())
                .title(training.getTitle())
                .description(training.getDescription())
                .startDate(training.getStartDate())
                .endDate(training.getEndDate())
                .durationDays(training.getDurationDays())
                .capacity(training.getCapacity())
                .allowedDepartments(training.getAllowedDepartments())
                .status(training.getStatus())
                .build();
    }

    public static EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .trainingId(enrollment.getTrainingId())
                .trainingTitle(enrollment.getTrainingTitle())
                .employeeId(enrollment.getEmployeeId())
                .employeeName(enrollment.getEmployeeName())
                .department(enrollment.getDepartment())
                .status(enrollment.getStatus())
                .requestedAt(enrollment.getRequestedAt())
                .reviewedAt(enrollment.getReviewedAt())
                .reviewedBy(enrollment.getReviewedBy())
                .rejectionReason(enrollment.getRejectionReason())
                .build();
    }
}