package com.example.TrainingService.service;

import com.example.TrainingService.dto.*;
import com.example.TrainingService.entity.Enrollment;
import com.example.TrainingService.entity.EnrollmentStatus;
import com.example.TrainingService.entity.Training;
import com.example.TrainingService.entity.TrainingStatus;
import com.example.TrainingService.exception.*;
import com.example.TrainingService.mapper.TrainingMapper;
import com.example.TrainingService.repository.EnrollmentRepository;
import com.example.TrainingService.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BusinessIdGeneratorService businessIdGeneratorService;

    public TrainingResponse createTraining(TrainingCreateRequest request) {
        validateTrainingDates(request.getStartDate(), request.getEndDate());
        validateCapacity(request.getCapacity());
        validateAllowedDepartments(request.getAllowedDepartments());

        Training training = TrainingMapper.toEntity(request);
        training.setTrainingId(businessIdGeneratorService.generateTrainingId());
        training.setDurationDays((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);

        Training saved = trainingRepository.save(training);
        return TrainingMapper.toResponse(saved);
    }

    public TrainingResponse getByTrainingId(String trainingId) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        return TrainingMapper.toResponse(training);
    }

    public Page<TrainingResponse> getAll(TrainingStatus status, String search, Pageable pageable) {
        return trainingRepository.searchTrainings(status, search, pageable)
                .map(TrainingMapper::toResponse);
    }

    public Page<TrainingEnrollmentResponse> getAvailableForDepartment(String department, String search, Pageable pageable) {
        return trainingRepository.findAvailableForDepartment(department, search, pageable)
                .map(TrainingMapper::toEnrollmentTrainingResponse);
    }

    public TrainingResponse updateTraining(String trainingId, TrainingUpdateRequest request) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        if (training.getStatus() == TrainingStatus.CANCELLED) {
            throw new TrainingAlreadyCancelledException("Cancelled training cannot be updated");
        }

        validateTrainingDates(request.getStartDate(), request.getEndDate());
        validateCapacity(request.getCapacity());
        validateAllowedDepartments(request.getAllowedDepartments());

        long approvedCount = enrollmentRepository.countByTrainingIdAndStatus(trainingId, EnrollmentStatus.APPROVED);
        if (request.getCapacity() < approvedCount) {
            throw new TrainingCapacityLessThanApprovedException("Training capacity cannot be less than approved enrollments");
        }

        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setStartDate(request.getStartDate());
        training.setEndDate(request.getEndDate());
        training.setDurationDays((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);
        training.setCapacity(request.getCapacity());
        training.setAllowedDepartments(request.getAllowedDepartments());

        Training updated = trainingRepository.save(training);
        return TrainingMapper.toResponse(updated);
    }

    public TrainingResponse cancelTraining(String trainingId) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        if (training.getStatus() == TrainingStatus.COMPLETED) {
            throw new CompletedTrainingCannotBeCancelledException("Completed training cannot be cancelled");
        }

        if (training.getStatus() == TrainingStatus.CANCELLED) {
            throw new TrainingAlreadyCancelledException("Training is already cancelled");
        }

        training.setStatus(TrainingStatus.CANCELLED);

        Training updated = trainingRepository.save(training);
        return TrainingMapper.toResponse(updated);
    }

    public EnrollmentResponse createEnrollment(String trainingId, EnrollmentCreateRequest request) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        if (training.getStatus() != TrainingStatus.UPCOMING) {
            throw new TrainingNotOpenForEnrollmentException("Training is not open for enrollment");
        }

        if (!isDepartmentAllowed(training.getAllowedDepartments(), request.getDepartment())) {
            throw new DepartmentNotAllowedException("Employee department is not allowed for this training");
        }

        boolean alreadyExists = enrollmentRepository.existsByEmployeeIdAndTrainingIdAndStatusIn(
                request.getEmployeeId(),
                trainingId,
                List.of(EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED)
        );

        if (alreadyExists) {
            throw new DuplicateEnrollmentException("Employee already has pending or approved enrollment for this training");
        }

        long employeeActiveEnrollments = enrollmentRepository.countByEmployeeIdAndStatusIn(
                request.getEmployeeId(),
                List.of(EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED)
        );

        if (employeeActiveEnrollments >= 3) {
            throw new MaxEnrollmentsExceededException("Employee reached maximum allowed active enrollments");
        }

        Enrollment enrollment = Enrollment.builder()
                .enrollmentId(businessIdGeneratorService.generateEnrollmentId())
                .trainingId(trainingId)
                .trainingTitle(training.getTitle())
                .employeeId(request.getEmployeeId())
                .employeeName(request.getEmployeeName())
                .department(request.getDepartment())
                .status(EnrollmentStatus.PENDING)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(saved);
    }

    public EnrollmentResponse getEnrollmentById(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        return TrainingMapper.toEnrollmentResponse(enrollment);
    }

    public Page<EnrollmentResponse> getEmployeeEnrollments(String employeeId, Pageable pageable) {
        return enrollmentRepository.findByEmployeeIdOrderByRequestedAtDesc(employeeId, pageable)
                .map(TrainingMapper::toEnrollmentResponse);
    }

    public Page<EnrollmentResponse> getTrainingEnrollments(String trainingId, EnrollmentStatus status, Pageable pageable) {
        if (status == null) {
            return enrollmentRepository.findByTrainingIdOrderByRequestedAtDesc(trainingId, pageable)
                    .map(TrainingMapper::toEnrollmentResponse);
        }

        return enrollmentRepository.findByTrainingIdAndStatusOrderByRequestedAtDesc(trainingId, status, pageable)
                .map(TrainingMapper::toEnrollmentResponse);
    }

    public EnrollmentResponse approveEnrollment(String enrollmentId, EnrollmentReviewRequest request) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new EnrollmentNotPendingException("Enrollment is not pending");
        }

        Training training = trainingRepository.findByTrainingId(enrollment.getTrainingId())
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + enrollment.getTrainingId()));

        if (training.getStatus() != TrainingStatus.UPCOMING) {
            throw new TrainingNotOpenForEnrollmentException("Training is not open for enrollment");
        }

        long approvedCount = enrollmentRepository.countByTrainingIdAndStatus(training.getTrainingId(), EnrollmentStatus.APPROVED);
        if (approvedCount >= training.getCapacity()) {
            throw new TrainingCapacityExceededException("Training capacity exceeded");
        }

        enrollment.setStatus(EnrollmentStatus.APPROVED);
        enrollment.setReviewedAt(LocalDateTime.now());
        enrollment.setReviewedBy(request.getReviewedBy());
        enrollment.setRejectionReason(null);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(updated);
    }

    public EnrollmentResponse rejectEnrollment(String enrollmentId, EnrollmentReviewRequest request) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new EnrollmentNotPendingException("Enrollment is not pending");
        }

        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
            throw new RejectionReasonRequiredException("Rejection reason is required");
        }

        enrollment.setStatus(EnrollmentStatus.REJECTED);
        enrollment.setReviewedAt(LocalDateTime.now());
        enrollment.setReviewedBy(request.getReviewedBy());
        enrollment.setRejectionReason(request.getRejectionReason());

        Enrollment updated = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(updated);
    }

    public long countPendingEnrollments() {
        return enrollmentRepository.countByStatus(EnrollmentStatus.PENDING);
    }

    private void validateTrainingDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidTrainingDateRangeException("End date must be after or equal to start date");
        }
    }

    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity < 1) {
            throw new InvalidTrainingCapacityException("Training capacity must be at least 1");
        }
    }

    private void validateAllowedDepartments(Set<String> allowedDepartments) {
        if (allowedDepartments == null || allowedDepartments.isEmpty()) {
            throw new InvalidAllowedDepartmentsException("At least one allowed department is required");
        }

        boolean invalid = allowedDepartments.stream().anyMatch(dep -> dep == null || dep.isBlank());
        if (invalid) {
            throw new InvalidAllowedDepartmentsException("Allowed departments contain invalid values");
        }
    }

    private boolean isDepartmentAllowed(Set<String> allowedDepartments, String department) {
        return allowedDepartments.stream()
                .anyMatch(dep -> dep.equalsIgnoreCase(department));
    }

    public long countTrainingsByStatus(TrainingStatus status) {
        return trainingRepository.countByStatus(status);
    }

    public long countEnrollmentsByStatus(EnrollmentStatus status) {
        return enrollmentRepository.countByStatus(status);
    }
}