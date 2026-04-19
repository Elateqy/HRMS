package com.hrms.leaveservice.service;

import com.hrms.leaveservice.entity.IdSequence;
import com.hrms.leaveservice.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessIdGeneratorService {

    private static final String LEAVE_SEQUENCE = "leave_sequence";

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public synchronized String generateLeaveId() {
        IdSequence sequence = idSequenceRepository.findById(LEAVE_SEQUENCE)
                .orElseGet(() -> IdSequence.builder()
                        .sequenceName(LEAVE_SEQUENCE)
                        .nextValue(1L)
                        .build());

        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return String.format("LEAVE-%04d", currentValue);
    }
}