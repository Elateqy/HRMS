package com.hrms.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "leaveservice")
public interface LeaveClient {

    @PostMapping("/leave-balances/{employeeId}/default")
    void createDefaultBalance(@PathVariable("employeeId") String employeeId);
}