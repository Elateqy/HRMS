package com.example.LeaveService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "id_sequences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdSequence {

    @Id
    @Column(name = "sequence_name", nullable = false, length = 100)
    private String sequenceName;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}