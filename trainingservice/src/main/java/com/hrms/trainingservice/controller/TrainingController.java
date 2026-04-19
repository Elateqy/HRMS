package com.hrms.trainingservice.controller;

import com.hrms.trainingservice.dto.*;
import com.hrms.trainingservice.entity.EnrollmentStatus;
import com.hrms.trainingservice.entity.TrainingStatus;
import com.hrms.trainingservice.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingResponse createTraining(@Valid @RequestBody TrainingCreateRequest request) {
        return trainingService.createTraining(request);
    }

    @GetMapping("/{trainingId}")
    public TrainingResponse getByTrainingId(@PathVariable String trainingId) {
        return trainingService.getByTrainingId(trainingId);
    }

    @GetMapping
    public Page<TrainingResponse> getAllTrainings(
            @RequestParam(required = false) TrainingStatus status,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return trainingService.getAll(status, search, pageable);
    }

    @GetMapping("/available")
    public Page<TrainingEnrollmentResponse> getAvailableForDepartment(
            @RequestParam String department,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return trainingService.getAvailableForDepartment(department, search, pageable);
    }

    @PutMapping("/{trainingId}")
    public TrainingResponse updateTraining(
            @PathVariable String trainingId,
            @Valid @RequestBody TrainingUpdateRequest request
    ) {
        return trainingService.updateTraining(trainingId, request);
    }

    @PutMapping("/{trainingId}/cancel")
    public TrainingResponse cancelTraining(@PathVariable String trainingId) {
        return trainingService.cancelTraining(trainingId);
    }

    @PostMapping("/{trainingId}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(
            @PathVariable String trainingId,
            @Valid @RequestBody EnrollmentCreateRequest request
    ) {
        return trainingService.createEnrollment(trainingId, request);
    }

    @PutMapping("/enrollments/{enrollmentId}/approve")
    public EnrollmentResponse approveEnrollment(
            @PathVariable String enrollmentId,
            @Valid @RequestBody EnrollmentReviewRequest request
    ) {
        return trainingService.approveEnrollment(enrollmentId, request.getReviewedBy());
    }

    @PutMapping("/enrollments/{enrollmentId}/reject")
    public EnrollmentResponse rejectEnrollment(
            @PathVariable String enrollmentId,
            @Valid @RequestBody EnrollmentReviewRequest request
    ) {
        return trainingService.rejectEnrollment(
                enrollmentId,
                request.getReviewedBy(),
                request.getRejectionReason()
        );
    }

    @GetMapping("/{trainingId}/enrollments")
    public PageResponse<EnrollmentResponse> getEnrollmentsByTrainingId(
            @PathVariable String trainingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return trainingService.getEnrollmentsByTrainingId(trainingId, page, size);
    }

    @GetMapping("/enrollments/employee/{employeeId}")
    public PageResponse<EnrollmentResponse> getEnrollmentsByEmployeeId(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return trainingService.getEnrollmentsByEmployeeId(employeeId, page, size);
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