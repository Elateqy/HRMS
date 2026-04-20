package com.hrms.reportingservice.repository;

import com.hrms.reportingservice.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {
}