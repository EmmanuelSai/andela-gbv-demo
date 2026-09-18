package com.andela.gbv.demo.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.andela.gbv.demo.entities.MediaAsset;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    List<MediaAsset> findByCaseId(UUID caseId);

    @Query("""
            SELECT m.id AS id, m.caseId AS caseId, m.contentType AS contentType,
                   m.sha256 AS sha256, m.sizeBytes AS sizeBytes, m.createdAt AS createdAt,
                   m.originalHadGps AS originalHadGps, m.originalHadExif AS originalHadExif,
                   m.originalHadXmp AS originalHadXmp
            FROM MediaAsset m
            WHERE m.caseId = :caseId
            """)
    List<MediaAssetMetadata> findMetadataByCaseId(@Param("caseId") UUID caseId);

    interface MediaAssetMetadata {
        UUID getId();

        UUID getCaseId();

        String getContentType();

        String getSha256();

        long getSizeBytes();

        Instant getCreatedAt();

        boolean isOriginalHadGps();

        boolean isOriginalHadExif();

        boolean isOriginalHadXmp();
    }
}
