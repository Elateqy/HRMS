package com.hrms.reportingservice.client;

import com.hrms.reportingservice.entity.EnrollmentStatus;
import com.hrms.reportingservice.entity.TrainingStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainingservice")
public interface TrainingClient {

    @GetMapping("/trainings/count/by-status")
    long countTrainingsByStatus(@RequestParam("status") TrainingStatus status);

    @GetMapping("/trainings/enrollments/count/by-status")
    long countEnrollmentsByStatus(@RequestParam("status") EnrollmentStatus status);
}