package com.example.ReportingService.repository;

import com.example.ReportingService.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {
}