package com.example.ReportingService.client;

import com.example.ReportingService.dto.ExternalEmployeeResponse;
import com.example.ReportingService.dto.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userservice")
public interface UserClient {

    @GetMapping("/users")
    PageResponse<ExternalEmployeeResponse> getEmployees(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("search") String search
    );

    @GetMapping("/users/count")
    long countEmployees();
}