package com.example.TrainingService.repository;

import com.example.TrainingService.entity.Training;
import com.example.TrainingService.entity.TrainingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Optional<Training> findByTrainingId(String trainingId);

    @Query("""
            SELECT t
            FROM Training t
            WHERE (:status IS NULL OR t.status = :status)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(t.trainingId) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<Training> searchTrainings(
            @Param("status") TrainingStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT t
            FROM Training t
            WHERE t.status = com.example.TrainingService.entity.TrainingStatus.UPCOMING
              AND LOWER(:department) IN (
                    SELECT LOWER(dep)
                    FROM t.allowedDepartments dep
              )
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(t.trainingId) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<Training> findAvailableForDepartment(
            @Param("department") String department,
            @Param("search") String search,
            Pageable pageable
    );

    long countByStatus(TrainingStatus status);
}