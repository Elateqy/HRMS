package com.hrms.userservice.service;

import com.hrms.userservice.client.LeaveClient;
import com.hrms.userservice.entity.ProvisioningJobStatus;
import com.hrms.userservice.entity.ProvisioningJobType;
import com.hrms.userservice.entity.UserProvisioningJob;
import com.hrms.userservice.repository.UserProvisioningJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

    private final UserProvisioningJobRepository jobRepository;
    private final LeaveClient leaveClient;

    @Transactional
    public void enqueueCreateDefaultLeaveBalance(String employeeId) {
        UserProvisioningJob job = UserProvisioningJob.builder()
                .employeeId(employeeId)
                .jobType(ProvisioningJobType.CREATE_DEFAULT_LEAVE_BALANCE)
                .status(ProvisioningJobStatus.PENDING)
                .retryCount(0)
                .build();

        jobRepository.save(job);
        log.info("Provisioning job queued for employeeId={}", employeeId);
    }

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void processPendingJobs() {
        List<UserProvisioningJob> jobs = jobRepository.findTop20ByStatusOrderByCreatedAtAsc(ProvisioningJobStatus.PENDING);

        for (UserProvisioningJob job : jobs) {
            try {
                if (job.getJobType() == ProvisioningJobType.CREATE_DEFAULT_LEAVE_BALANCE) {
                    leaveClient.createDefaultBalance(job.getEmployeeId());
                }

                job.setStatus(ProvisioningJobStatus.COMPLETED);
                job.setLastError(null);
                log.info("Provisioning job completed for employeeId={}", job.getEmployeeId());
            } catch (Exception ex) {
                job.setRetryCount(job.getRetryCount() + 1);
                job.setLastError(ex.getMessage());

                if (job.getRetryCount() >= 10) {
                    job.setStatus(ProvisioningJobStatus.FAILED);
                }

                log.error("Provisioning job failed for employeeId={} retryCount={}",
                        job.getEmployeeId(),
                        job.getRetryCount(),
                        ex);
            }
        }
    }
}