package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.utils.AesEncryptionConverter;

@Data
@Entity
@Table(name = "safety_vault", indexes = {
        @Index(name = "idx_safety_vault_case_id", columnList = "case_id")
})
public class SafetyVault {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Convert(converter = AesEncryptionConverter.class)
    @Column(name = "encrypted_blob", columnDefinition = "TEXT")
    private String encryptedBlob;

    @Column(name = "access_token_hash", length = 128)
    private String accessTokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
