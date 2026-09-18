package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.entities.Referral;
import com.andela.gbv.demo.models.ReferralStatus;

public record ReferralDto(
        UUID id,
        UUID caseId,
        String serviceType,
        String serviceId,
        ReferralStatus status,
        Instant createdAt) {
    public static ReferralDto from(Referral r) {
        return new ReferralDto(
                r.getId(),
                r.getCaseId(),
                r.getServiceType(),
                r.getServiceId(),
                r.getStatus(),
                r.getCreatedAt());
    }
}
