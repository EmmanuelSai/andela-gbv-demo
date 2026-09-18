package com.andela.gbv.demo.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.WitnessContact;

public interface WitnessContactRepository extends JpaRepository<WitnessContact, UUID> {
    List<WitnessContact> findByCaseId(UUID caseId);

}
