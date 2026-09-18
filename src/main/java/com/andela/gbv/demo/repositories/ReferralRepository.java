package com.andela.gbv.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.Referral;

public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    List<Referral> findByCaseId(UUID caseId);
}
