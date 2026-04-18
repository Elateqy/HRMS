package com.example.TrainingService.client;

import com.example.TrainingService.dto.ExternalEmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userservice", path = "/users")
public interface UserClient {

    @GetMapping("/{employeeId}")
    ExternalEmployeeResponse getEmployeeById(@PathVariable("employeeId") String employeeId);
}