package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.entities.AuditLog;

public record AuditLogDto(
        UUID id,
        String actor,
        String action,
        UUID caseId,
        String detail,
        Instant at) {
    public static AuditLogDto from(AuditLog log) {
        return new AuditLogDto(
                log.getId(),
                log.getActor(),
                log.getAction(),
                log.getCaseId(),
                log.getDetail(),
                log.getAt());
    }
}
