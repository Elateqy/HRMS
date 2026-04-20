package com.hrms.reportingservice.service;

import com.hrms.reportingservice.entity.IdSequence;
import com.hrms.reportingservice.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessIdGeneratorService {

    private static final String REPORT_SEQUENCE = "report_sequence";

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public synchronized String generateReportId() {
        IdSequence sequence = idSequenceRepository.findById(REPORT_SEQUENCE)
                .orElseGet(() -> IdSequence.builder()
                        .sequenceName(REPORT_SEQUENCE)
                        .nextValue(1L)
                        .build());

        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return String.format("RPT-%04d", currentValue);
    }
}