package com.andela.gbv.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.andela.gbv.demo.entities.Case;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;

public interface CaseRepository extends JpaRepository<Case, UUID>, JpaSpecificationExecutor<Case> {
    long countByType(CaseType type);

    long countByStatus(CaseStatus status);

    long countByFemicideRisk(FemicideRiskLevel level);

    @Query("SELECT c.status, COUNT(c) FROM GbvCase c GROUP BY c.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT c.type, COUNT(c) FROM GbvCase c GROUP BY c.type")
    List<Object[]> countGroupedByType();

    @Query("SELECT c.femicideRisk, COUNT(c) FROM GbvCase c GROUP BY c.femicideRisk")
    List<Object[]> countGroupedByRisk();

    @Query("""
            SELECT c FROM GbvCase c
            WHERE c.femicideRisk = :risk OR c.status = :status
            ORDER BY c.createdAt DESC
            """)
    List<Case> findPriorityQueue(
            @Param("risk") FemicideRiskLevel risk,
            @Param("status") CaseStatus status,
            Pageable pageable);
}
