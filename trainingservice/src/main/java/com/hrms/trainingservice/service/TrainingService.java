package com.hrms.trainingservice.service;

import com.hrms.trainingservice.client.UserClient;
import com.hrms.trainingservice.dto.*;
import com.hrms.trainingservice.entity.Enrollment;
import com.hrms.trainingservice.entity.EnrollmentStatus;
import com.hrms.trainingservice.entity.Training;
import com.hrms.trainingservice.entity.TrainingStatus;
import com.hrms.trainingservice.exception.*;
import com.hrms.trainingservice.mapper.TrainingMapper;
import com.hrms.trainingservice.repository.EnrollmentRepository;
import com.hrms.trainingservice.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final UserClient userClient;
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
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<Training> resultPage;
        if (status != null && normalizedSearch != null) {
            resultPage = trainingRepository.findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                    status,
                    normalizedSearch,
                    pageable
            );
        } else if (status != null) {
            resultPage = trainingRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (normalizedSearch != null) {
            resultPage = trainingRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                    normalizedSearch,
                    pageable
            );
        } else {
            resultPage = trainingRepository.findAll(pageable);
        }

        return resultPage.map(TrainingMapper::toResponse);
    }

    public Page<TrainingEnrollmentResponse> getAvailableForDepartment(String department, String search, Pageable pageable) {
        String normalizedDepartment = department == null ? null : department.trim();
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<Training> resultPage;
        if (normalizedSearch != null) {
            resultPage = trainingRepository
                    .findByStatusAndAllowedDepartmentsContainingIgnoreCaseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                            TrainingStatus.UPCOMING,
                            normalizedDepartment,
                            normalizedSearch,
                            pageable
                    );
        } else {
            resultPage = trainingRepository.findByStatusAndAllowedDepartmentsContainingIgnoreCaseOrderByCreatedAtDesc(
                    TrainingStatus.UPCOMING,
                    normalizedDepartment,
                    pageable
            );
        }

        return resultPage.map(TrainingMapper::toEnrollmentTrainingResponse);
    }

    public TrainingResponse updateTraining(String trainingId, TrainingUpdateRequest request) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        validateTrainingDates(request.getStartDate(), request.getEndDate());
        validateCapacity(request.getCapacity());
        validateAllowedDepartments(request.getAllowedDepartments());

        long approvedEnrollments = enrollmentRepository.countByTrainingIdAndStatus(
                trainingId,
                EnrollmentStatus.APPROVED
        );

        if (request.getCapacity() < approvedEnrollments) {
            throw new TrainingCapacityLessThanApprovedException(
                    "Capacity cannot be less than approved enrollments count"
            );
        }

        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setStartDate(request.getStartDate());
        training.setEndDate(request.getEndDate());
        training.setCapacity(request.getCapacity());
        training.setAllowedDepartments(request.getAllowedDepartments());
        training.setDurationDays((int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);

        Training updated = trainingRepository.save(training);
        return TrainingMapper.toResponse(updated);
    }

    public TrainingResponse cancelTraining(String trainingId) {
        Training training = trainingRepository.findByTrainingId(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + trainingId));

        if (training.getStatus() == TrainingStatus.CANCELLED) {
            throw new TrainingAlreadyCancelledException("Training already cancelled");
        }

        if (training.getStatus() == TrainingStatus.COMPLETED) {
            throw new CompletedTrainingCannotBeCancelledException("Completed training cannot be cancelled");
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

        ExternalEmployeeResponse employee = userClient.getEmployeeById(request.getEmployeeId());

        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found: " + request.getEmployeeId());
        }

        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new InactiveEmployeeException("Employee is inactive: " + request.getEmployeeId());
        }

        if (!training.getAllowedDepartments().contains(employee.getDepartment())) {
            throw new DepartmentNotAllowedException("Employee department is not allowed for this training");
        }

        if (enrollmentRepository.existsByTrainingIdAndEmployeeId(trainingId, request.getEmployeeId())) {
            throw new DuplicateEnrollmentException("Employee already enrolled in this training");
        }

        long approvedCount = enrollmentRepository.countByTrainingIdAndStatus(trainingId, EnrollmentStatus.APPROVED);
        if (approvedCount >= training.getCapacity()) {
            throw new TrainingCapacityExceededException("Training capacity exceeded");
        }

        Enrollment enrollment = TrainingMapper.toEnrollmentEntity(training, employee);
        enrollment.setEnrollmentId(businessIdGeneratorService.generateEnrollmentId());
        enrollment.setStatus(EnrollmentStatus.PENDING);
        enrollment.setReviewedAt(null);
        enrollment.setReviewedBy(null);
        enrollment.setRejectionReason(null);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(saved);
    }

    public EnrollmentResponse approveEnrollment(String enrollmentId, String reviewedBy) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new EnrollmentNotPendingException("Enrollment is not pending");
        }

        Training training = trainingRepository.findByTrainingId(enrollment.getTrainingId())
                .orElseThrow(() -> new ResourceNotFoundException("Training not found: " + enrollment.getTrainingId()));

        long approvedCount = enrollmentRepository.countByTrainingIdAndStatus(
                enrollment.getTrainingId(),
                EnrollmentStatus.APPROVED
        );

        if (approvedCount >= training.getCapacity()) {
            throw new MaxEnrollmentsExceededException("Training capacity exceeded");
        }

        enrollment.setStatus(EnrollmentStatus.APPROVED);
        enrollment.setReviewedAt(LocalDateTime.now());
        enrollment.setReviewedBy(reviewedBy);
        enrollment.setRejectionReason(null);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(updated);
    }

    public EnrollmentResponse rejectEnrollment(String enrollmentId, String reviewedBy, String rejectionReason) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new EnrollmentNotPendingException("Enrollment is not pending");
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new RejectionReasonRequiredException("Rejection reason is required");
        }

        enrollment.setStatus(EnrollmentStatus.REJECTED);
        enrollment.setReviewedAt(LocalDateTime.now());
        enrollment.setReviewedBy(reviewedBy);
        enrollment.setRejectionReason(rejectionReason);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return TrainingMapper.toEnrollmentResponse(updated);
    }

    public PageResponse<EnrollmentResponse> getEnrollmentsByTrainingId(String trainingId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));

        Page<Enrollment> resultPage = enrollmentRepository.findByTrainingIdOrderByRequestedAtDesc(trainingId, pageable);

        return PageResponse.<EnrollmentResponse>builder()
                .content(resultPage.getContent().stream().map(TrainingMapper::toEnrollmentResponse).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    public PageResponse<EnrollmentResponse> getEnrollmentsByEmployeeId(String employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));

        Page<Enrollment> resultPage = enrollmentRepository.findByEmployeeIdOrderByRequestedAtDesc(employeeId, pageable);

        return PageResponse.<EnrollmentResponse>builder()
                .content(resultPage.getContent().stream().map(TrainingMapper::toEnrollmentResponse).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    public long countTrainingsByStatus(TrainingStatus status) {
        return trainingRepository.countByStatus(status);
    }

    public long countEnrollmentsByStatus(EnrollmentStatus status) {
        return enrollmentRepository.countByStatus(status);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateTrainingStatuses() {
        LocalDate today = LocalDate.now();
        List<Training> trainings = trainingRepository.findAll();

        for (Training training : trainings) {
            if (training.getStatus() == TrainingStatus.CANCELLED) {
                continue;
            }

            if (today.isBefore(training.getStartDate())) {
                training.setStatus(TrainingStatus.UPCOMING);
            } else if (!today.isAfter(training.getEndDate())) {
                training.setStatus(TrainingStatus.ONGOING);
            } else {
                training.setStatus(TrainingStatus.COMPLETED);
            }
        }
    }

    private void validateTrainingDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidTrainingDateRangeException("End date must be after or equal to start date");
        }
    }

    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new InvalidTrainingCapacityException("Capacity must be greater than zero");
        }
    }

    private void validateAllowedDepartments(Set<String> allowedDepartments) {
        if (allowedDepartments == null || allowedDepartments.isEmpty()) {
            throw new InvalidAllowedDepartmentsException("Allowed departments are required");
        }

        boolean hasBlank = allowedDepartments.stream().anyMatch(dept -> dept == null || dept.isBlank());
        if (hasBlank) {
            throw new InvalidAllowedDepartmentsException("Allowed departments must not contain blank values");
        }
    }
}