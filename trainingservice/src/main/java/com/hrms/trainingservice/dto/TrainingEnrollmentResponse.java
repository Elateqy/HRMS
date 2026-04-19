package com.hrms.trainingservice.dto;

import com.hrms.trainingservice.entity.TrainingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingEnrollmentResponse {

    private String trainingId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private Integer capacity;
    private Set<String> allowedDepartments;
    private TrainingStatus status;
}