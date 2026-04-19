package com.example.TrainingService.controller;

import com.example.TrainingService.dto.*;
import com.example.TrainingService.entity.EnrollmentStatus;
import com.example.TrainingService.entity.TrainingStatus;
import com.example.TrainingService.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@Tag(name = "Training Controller", description = "APIs for trainings and enrollments")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "Create training")
    public TrainingResponse createTraining(@Valid @RequestBody TrainingCreateRequest request) {
        return trainingService.createTraining(request);
    }

    @GetMapping("/{trainingId}")
    @Operation(summary = "Get training by training ID")
    public TrainingResponse getByTrainingId(@PathVariable String trainingId) {
        return trainingService.getByTrainingId(trainingId);
    }

    @GetMapping
    @Operation(summary = "Search trainings")
    public PageResponse<TrainingResponse> getAll(
            @RequestParam(required = false) TrainingStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TrainingResponse> result = trainingService.getAll(status, search, PageRequest.of(page, size));
        return PageResponse.<TrainingResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @GetMapping("/available")
    @Operation(summary = "Get trainings available for department")
    public PageResponse<TrainingEnrollmentResponse> getAvailableForDepartment(
            @RequestParam String department,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TrainingEnrollmentResponse> result =
                trainingService.getAvailableForDepartment(department, search, PageRequest.of(page, size));

        return PageResponse.<TrainingEnrollmentResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @PutMapping("/{trainingId}")
    @Operation(summary = "Update training")
    public TrainingResponse updateTraining(@PathVariable String trainingId,
                                           @Valid @RequestBody TrainingUpdateRequest request) {
        return trainingService.updateTraining(trainingId, request);
    }

    @PutMapping("/{trainingId}/cancel")
    @Operation(summary = "Cancel training")
    public TrainingResponse cancelTraining(@PathVariable String trainingId) {
        return trainingService.cancelTraining(trainingId);
    }

    @PostMapping("/{trainingId}/enroll")
    @Operation(summary = "Create enrollment request")
    public EnrollmentResponse createEnrollment(@PathVariable String trainingId,
                                               @Valid @RequestBody EnrollmentCreateRequest request) {
        return trainingService.createEnrollment(trainingId, request);
    }

    @GetMapping("/enrollments/{enrollmentId}")
    @Operation(summary = "Get enrollment by enrollment ID")
    public EnrollmentResponse getEnrollmentById(@PathVariable String enrollmentId) {
        return trainingService.getEnrollmentById(enrollmentId);
    }

    @GetMapping("/enrollments/employee/{employeeId}")
    @Operation(summary = "Get employee enrollments")
    public PageResponse<EnrollmentResponse> getEmployeeEnrollments(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EnrollmentResponse> result =
                trainingService.getEmployeeEnrollments(employeeId, PageRequest.of(page, size));

        return PageResponse.<EnrollmentResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @GetMapping("/{trainingId}/enrollments")
    @Operation(summary = "Get enrollments by training")
    public PageResponse<EnrollmentResponse> getTrainingEnrollments(
            @PathVariable String trainingId,
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EnrollmentResponse> result =
                trainingService.getTrainingEnrollments(trainingId, status, PageRequest.of(page, size));

        return PageResponse.<EnrollmentResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @PutMapping("/enrollments/{enrollmentId}/approve")
    @Operation(summary = "Approve enrollment")
    public EnrollmentResponse approveEnrollment(@PathVariable String enrollmentId,
                                                @Valid @RequestBody EnrollmentReviewRequest request) {
        return trainingService.approveEnrollment(enrollmentId, request);
    }

    @PutMapping("/enrollments/{enrollmentId}/reject")
    @Operation(summary = "Reject enrollment")
    public EnrollmentResponse rejectEnrollment(@PathVariable String enrollmentId,
                                               @Valid @RequestBody EnrollmentReviewRequest request) {
        return trainingService.rejectEnrollment(enrollmentId, request);
    }

    @GetMapping("/enrollments/count/pending")
    @Operation(summary = "Count pending enrollments")
    public long countPendingEnrollments() {
        return trainingService.countPendingEnrollments();
    }

    @GetMapping("/count/by-status")
    public long countTrainingsByStatus(@RequestParam TrainingStatus status) {
        return trainingService.countTrainingsByStatus(status);
    }

    @GetMapping("/enrollments/count/by-status")
    public long countEnrollmentsByStatus(@RequestParam EnrollmentStatus status) {
        return trainingService.countEnrollmentsByStatus(status);
    }
}