package com.hrms.userservice.repository;

import com.hrms.userservice.entity.ProvisioningJobStatus;
import com.hrms.userservice.entity.UserProvisioningJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProvisioningJobRepository extends JpaRepository<UserProvisioningJob, Long> {

    List<UserProvisioningJob> findTop20ByStatusOrderByCreatedAtAsc(ProvisioningJobStatus status);
}