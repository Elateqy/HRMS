package com.hrms.reportingservice.client;

import com.hrms.reportingservice.entity.LeaveStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "leaveservice")
public interface LeaveClient {

    @GetMapping("/leaves/count/by-status")
    long countLeavesByStatus(@RequestParam("status") LeaveStatus status);
}