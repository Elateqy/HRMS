package com.example.TrainingService.repository;

import com.example.TrainingService.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {
}