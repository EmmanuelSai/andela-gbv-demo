package com.andela.gbv.demo.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "media_assets", indexes = {
        @Index(name = "idx_media_assets_case_id", columnList = "case_id")
})
public class MediaAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Lob
    @Column(name = "sanitized_data", nullable = false, columnDefinition = "BYTEA")
    private byte[] sanitizedData;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(name = "sha256", nullable = false, length = 64)
    private String sha256;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "original_had_gps")
    private boolean originalHadGps;

    @Column(name = "original_had_exif")
    private boolean originalHadExif;

    @Column(name = "original_had_xmp")
    private boolean originalHadXmp;
}
