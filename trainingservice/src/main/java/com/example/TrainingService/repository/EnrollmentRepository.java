package com.example.TrainingService.repository;

import com.example.TrainingService.entity.Enrollment;
import com.example.TrainingService.entity.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByEnrollmentId(String enrollmentId);

    Page<Enrollment> findByEmployeeIdOrderByRequestedAtDesc(String employeeId, Pageable pageable);

    Page<Enrollment> findByTrainingIdOrderByRequestedAtDesc(String trainingId, Pageable pageable);

    Page<Enrollment> findByTrainingIdAndStatusOrderByRequestedAtDesc(String trainingId, EnrollmentStatus status, Pageable pageable);

    boolean existsByEmployeeIdAndTrainingIdAndStatusIn(String employeeId, String trainingId, Collection<EnrollmentStatus> statuses);

    long countByEmployeeIdAndStatusIn(String employeeId, Collection<EnrollmentStatus> statuses);

    long countByTrainingIdAndStatus(String trainingId, EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);
}