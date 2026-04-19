package com.hrms.trainingservice.repository;

import com.hrms.trainingservice.entity.Training;
import com.hrms.trainingservice.entity.TrainingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Optional<Training> findByTrainingId(String trainingId);

    long countByStatus(TrainingStatus status);

    Page<Training> findByStatusOrderByCreatedAtDesc(TrainingStatus status, Pageable pageable);

    Page<Training> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);

    Page<Training> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            TrainingStatus status,
            String title,
            Pageable pageable
    );

    Page<Training> findByStatusAndAllowedDepartmentsContainingIgnoreCaseOrderByCreatedAtDesc(
            TrainingStatus status,
            String department,
            Pageable pageable
    );

    Page<Training> findByStatusAndAllowedDepartmentsContainingIgnoreCaseAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            TrainingStatus status,
            String department,
            String title,
            Pageable pageable
    );
}