package com.andela.gbv.demo.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByCaseIdOrderByAtDesc(UUID caseId, Pageable pageable);

    Page<AuditLog> findAllByOrderByAtDesc(Pageable pageable);
}
