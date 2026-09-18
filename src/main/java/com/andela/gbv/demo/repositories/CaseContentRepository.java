package com.andela.gbv.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.CaseContent;

public interface CaseContentRepository extends JpaRepository<CaseContent, UUID> {
    List<CaseContent> findByCaseId(UUID caseId);
}
