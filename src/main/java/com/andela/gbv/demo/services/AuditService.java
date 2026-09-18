package com.andela.gbv.demo.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.andela.gbv.demo.entities.AuditLog;
import com.andela.gbv.demo.repositories.AuditLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public void record(String actor, String action, UUID caseId, String detail) {
        AuditLog entry = new AuditLog();
        entry.setActor(actor);
        entry.setAction(action);
        entry.setCaseId(caseId);
        entry.setDetail(truncate(detail));
        auditLogRepository.save(entry);
        log.debug("Audit {} by {} on case {}", action, actor, caseId);
    }

    private String truncate(String detail) {
        if (detail == null) {
            return null;
        }
        return detail.length() <= 500 ? detail : detail.substring(0, 500);
    }
}
