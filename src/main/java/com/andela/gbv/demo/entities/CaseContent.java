package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

import com.andela.gbv.demo.utils.AesEncryptionConverter;

@Data
@Entity
@Table(name = "case_content", indexes = {
        @Index(name = "idx_case_content_case_id", columnList = "case_id")
})
public class CaseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Convert(converter = AesEncryptionConverter.class)
    @Column(columnDefinition = "TEXT")
    private String encryptedText;

    @Column(length = 10)
    private String language = "en";
}
