package com.hrms.userservice.service;

import com.hrms.userservice.entity.IdSequence;
import com.hrms.userservice.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessIdGeneratorService {

    private static final String EMPLOYEE_SEQUENCE = "employee_sequence";

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public synchronized String generateEmployeeId() {
        IdSequence sequence = idSequenceRepository.findById(EMPLOYEE_SEQUENCE)
                .orElseGet(() -> IdSequence.builder()
                        .sequenceName(EMPLOYEE_SEQUENCE)
                        .nextValue(1L)
                        .build());

        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return String.format("EMP-%04d", currentValue);
    }
}