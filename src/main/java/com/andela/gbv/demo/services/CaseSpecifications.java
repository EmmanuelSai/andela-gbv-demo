package com.andela.gbv.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.andela.gbv.demo.entities.Case;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;

import jakarta.persistence.criteria.Predicate;

public final class CaseSpecifications {
    private CaseSpecifications() {
    }

    public static Specification<Case> withFilters(
            CaseStatus status,
            CaseType type,
            FemicideRiskLevel risk,
            String assignedTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (risk != null) {
                predicates.add(cb.equal(root.get("femicideRisk"), risk));
            }
            if (assignedTo != null && !assignedTo.isBlank()) {
                predicates.add(cb.equal(root.get("assignedTo"), assignedTo.trim()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
