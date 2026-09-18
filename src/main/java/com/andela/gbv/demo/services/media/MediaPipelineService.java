package com.andela.gbv.demo.services.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andela.gbv.demo.entities.MediaAsset;
import com.andela.gbv.demo.repositories.MediaAssetRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaPipelineService {
    private final MediaService mediaService;
    private final MediaAssetRepository mediaAssetRepository;

    @Transactional
    public UUID ingestMedia(UUID caseId, String twilioMediaUrl, String declaredContentType) {
        MetadataSanitizer.SanitizedMedia sanitized = mediaService.fetchAndSanitize(twilioMediaUrl, declaredContentType);

        MediaAsset asset = new MediaAsset();
        asset.setCaseId(caseId);
        asset.setSanitizedData(sanitized.bytes());
        asset.setContentType(sanitized.contentType());
        asset.setSha256(sanitized.sha256());
        asset.setSizeBytes(sanitized.bytes().length);
        asset.setOriginalHadGps(sanitized.audit().hadGps);
        asset.setOriginalHadExif(sanitized.audit().hadExif);
        asset.setOriginalHadXmp(sanitized.audit().hadXmp);

        mediaAssetRepository.save(asset);
        log.info("Media ingested: case={}, asset={}, sha256={}, gps_stripped={}",
                caseId, asset.getId(), asset.getSha256(), sanitized.audit().hadGps);
        return asset.getId();
    }
}
