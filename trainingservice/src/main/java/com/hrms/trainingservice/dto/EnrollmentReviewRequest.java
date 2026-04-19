package com.hrms.trainingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentReviewRequest {

    @NotBlank(message = "Reviewed by is required")
    @Size(max = 100, message = "Reviewed by must not exceed 100 characters")
    private String reviewedBy;

    @Size(max = 3000, message = "Rejection reason must not exceed 3000 characters")
    private String rejectionReason;
}