package com.example.LeaveService.client;

import com.example.LeaveService.dto.ExternalEmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userservice")
public interface UserClient {

    @GetMapping("/users/{employeeId}")
    ExternalEmployeeResponse getEmployeeById(@PathVariable("employeeId") String employeeId);
}