package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideIndicators;
import com.andela.gbv.demo.models.FemicideRiskLevel;

public record CaseDetailDto(
        UUID id,
        CaseType type,
        CaseStatus status,
        FemicideRiskLevel femicideRisk,
        FemicideIndicators riskIndicators,
        String assignedTo,
        Instant createdAt,
        Instant updatedAt,
        List<CaseContentDto> contents,
        List<WitnessContactDto> witnessContacts,
        List<ReferralDto> referrals,
        List<MediaMetadataDto> media,
        int vaultCount) {
}
