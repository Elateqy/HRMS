package com.hrms.trainingservice.mapper;

import com.hrms.trainingservice.dto.*;
import com.hrms.trainingservice.entity.Enrollment;
import com.hrms.trainingservice.entity.Training;

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
                .status(training.getStatus())
                .allowedDepartments(training.getAllowedDepartments())
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
                .status(training.getStatus())
                .allowedDepartments(training.getAllowedDepartments())
                .build();
    }

    public static Enrollment toEnrollmentEntity(Training training, ExternalEmployeeResponse employee) {
        String fullName = employee.getFullName();

        if (fullName == null || fullName.isBlank()) {
            String firstName = employee.getFirstName() == null ? "" : employee.getFirstName().trim();
            String lastName = employee.getLastName() == null ? "" : employee.getLastName().trim();
            fullName = (firstName + " " + lastName).trim();
        }

        return Enrollment.builder()
                .trainingId(training.getTrainingId())
                .trainingTitle(training.getTitle())
                .employeeId(employee.getEmployeeId())
                .employeeName(fullName)
                .department(employee.getDepartment())
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