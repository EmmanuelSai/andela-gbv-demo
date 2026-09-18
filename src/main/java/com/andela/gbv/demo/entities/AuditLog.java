package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "audit_log", indexes = {
        @Index(name = "idx_audit_log_case_id", columnList = "case_id"),
        @Index(name = "idx_audit_log_at", columnList = "at")
})
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actor", nullable = false, length = 100)
    private String actor;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "detail", length = 500)
    private String detail;

    @Column(name = "at", nullable = false)
    private Instant at = Instant.now();
}
