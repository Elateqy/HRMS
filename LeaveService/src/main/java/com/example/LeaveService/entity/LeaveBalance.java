package com.example.LeaveService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    private String employeeId;

    @Column(name = "annual_remaining", nullable = false)
    private Integer annualRemaining;

    @Column(name = "sick_remaining", nullable = false)
    private Integer sickRemaining;

    @Column(name = "casual_remaining", nullable = false)
    private Integer casualRemaining;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (annualRemaining == null) {
            annualRemaining = 21;
        }
        if (sickRemaining == null) {
            sickRemaining = 10;
        }
        if (casualRemaining == null) {
            casualRemaining = 7;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}