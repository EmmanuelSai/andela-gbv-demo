package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideIndicators;
import com.andela.gbv.demo.models.FemicideRiskLevel;
import com.andela.gbv.demo.utils.RiskIndicatorsConverter;

@Data
@Entity(name = "GbvCase")
@Table(name = "cases", indexes = {
        @Index(name = "idx_cases_status", columnList = "status"),
        @Index(name = "idx_cases_femicide_risk", columnList = "femicide_risk"),
        @Index(name = "idx_cases_created_at", columnList = "created_at"),
        @Index(name = "idx_cases_assigned_to", columnList = "assigned_to")
})
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CaseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status = CaseStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "femicide_risk", length = 20)
    private FemicideRiskLevel femicideRisk = FemicideRiskLevel.UNKNOWN;

    @Convert(converter = RiskIndicatorsConverter.class)
    @Column(name = "risk_indicators", columnDefinition = "TEXT")
    private FemicideIndicators riskIndicators = new FemicideIndicators();

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (riskIndicators == null) {
            riskIndicators = new FemicideIndicators();
        }
        if (status == null) {
            status = CaseStatus.NEW;
        }
        if (femicideRisk == null) {
            femicideRisk = FemicideRiskLevel.UNKNOWN;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
