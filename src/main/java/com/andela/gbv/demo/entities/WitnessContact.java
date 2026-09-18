package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.utils.AesEncryptionConverter;

@Data
@Entity
@Table(name = "witness_contacts", indexes = {
        @Index(name = "idx_witness_contacts_case_id", columnList = "case_id")
})
public class WitnessContact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Convert(converter = AesEncryptionConverter.class)
    @Column(name = "safe_contact", columnDefinition = "TEXT")
    private String safeContact;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
