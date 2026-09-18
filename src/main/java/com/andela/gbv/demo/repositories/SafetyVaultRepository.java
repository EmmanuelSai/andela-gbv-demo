package com.andela.gbv.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.SafetyVault;

public interface SafetyVaultRepository extends JpaRepository<SafetyVault, UUID> {
    List<SafetyVault> findByCaseId(UUID caseId);

    long countByCaseId(UUID caseId);
}
