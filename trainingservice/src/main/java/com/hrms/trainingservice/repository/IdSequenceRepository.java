package com.hrms.trainingservice.repository;

import com.hrms.trainingservice.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {
}