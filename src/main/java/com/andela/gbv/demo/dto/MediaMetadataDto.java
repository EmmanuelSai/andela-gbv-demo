package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.repositories.MediaAssetRepository.MediaAssetMetadata;

public record MediaMetadataDto(
        UUID id,
        String contentType,
        String sha256,
        long sizeBytes,
        Instant createdAt,
        boolean originalHadGps,
        boolean originalHadExif,
        boolean originalHadXmp) {
    public static MediaMetadataDto from(MediaAssetMetadata m) {
        return new MediaMetadataDto(
                m.getId(),
                m.getContentType(),
                m.getSha256(),
                m.getSizeBytes(),
                m.getCreatedAt(),
                m.isOriginalHadGps(),
                m.isOriginalHadExif(),
                m.isOriginalHadXmp());
    }
}
