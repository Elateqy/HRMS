package com.hrms.trainingservice.service;

import com.hrms.trainingservice.entity.IdSequence;
import com.hrms.trainingservice.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessIdGeneratorService {

    private static final String TRAINING_SEQUENCE = "training_sequence";
    private static final String ENROLLMENT_SEQUENCE = "enrollment_sequence";

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public synchronized String generateTrainingId() {
        return generate(TRAINING_SEQUENCE, "TRN-%04d");
    }

    @Transactional
    public synchronized String generateEnrollmentId() {
        return generate(ENROLLMENT_SEQUENCE, "ENR-%04d");
    }

    private String generate(String sequenceName, String pattern) {
        IdSequence sequence = idSequenceRepository.findById(sequenceName)
                .orElseGet(() -> IdSequence.builder()
                        .sequenceName(sequenceName)
                        .nextValue(1L)
                        .build());

        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return String.format(pattern, currentValue);
    }
}