package com.hrms.leaveservice.repository;

import com.hrms.leaveservice.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}