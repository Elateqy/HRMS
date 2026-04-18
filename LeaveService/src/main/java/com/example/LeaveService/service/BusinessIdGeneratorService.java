package com.example.LeaveService.service;

import com.example.LeaveService.entity.IdSequence;
import com.example.LeaveService.repository.IdSequenceRepository;
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

        return String.format("LEV-%04d", currentValue);
    }
}