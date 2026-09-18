package com.andela.gbv.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andela.gbv.demo.entities.ProcessedMessage;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
    boolean existsByMessageSid(String sid);

}
