package com.example.ReportingService.client;

import com.example.ReportingService.entity.EnrollmentStatus;
import com.example.ReportingService.entity.TrainingStatus;
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