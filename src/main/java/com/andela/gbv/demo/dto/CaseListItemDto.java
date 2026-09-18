package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.entities.Case;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;

public record CaseListItemDto(
        UUID id,
        CaseType type,
        CaseStatus status,
        FemicideRiskLevel femicideRisk,
        String assignedTo,
        Instant createdAt,
        Instant updatedAt) {
    public static CaseListItemDto from(Case c) {
        return new CaseListItemDto(
                c.getId(),
                c.getType(),
                c.getStatus(),
                c.getFemicideRisk(),
                c.getAssignedTo(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
