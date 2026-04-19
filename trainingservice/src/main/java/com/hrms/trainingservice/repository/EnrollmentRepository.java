package com.hrms.trainingservice.repository;

import com.hrms.trainingservice.entity.Enrollment;
import com.hrms.trainingservice.entity.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByEnrollmentId(String enrollmentId);

    boolean existsByTrainingIdAndEmployeeId(String trainingId, String employeeId);

    long countByTrainingIdAndStatus(String trainingId, EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);

    Page<Enrollment> findByTrainingIdOrderByRequestedAtDesc(String trainingId, Pageable pageable);

    Page<Enrollment> findByEmployeeIdOrderByRequestedAtDesc(String employeeId, Pageable pageable);
}