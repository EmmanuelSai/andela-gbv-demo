package com.andela.gbv.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.models.ReferralStatus;

@Data
@Entity
@Table(name = "referrals", indexes = {
        @Index(name = "idx_referrals_case_id", columnList = "case_id")
})
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Column(name = "service_type", length = 50)
    private String serviceType;

    @Column(name = "service_id", length = 100)
    private String serviceId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ReferralStatus status = ReferralStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
