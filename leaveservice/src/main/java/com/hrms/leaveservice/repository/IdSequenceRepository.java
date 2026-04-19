package com.hrms.leaveservice.repository;

import com.hrms.leaveservice.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {
}