package com.andela.gbv.demo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.andela.gbv.demo.entities.ProcessedMessage;
import com.andela.gbv.demo.repositories.ProcessedMessageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageIdempotencyService {
private final ProcessedMessageRepository repository;

    public boolean alreadyProcessed(String messageSid) {
        return repository.existsById(messageSid);
    }

    /**
     * REQUIRES_NEW so the mark survives even if the caller's transaction
     * rolls back. Do not change this without understanding the implications.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(String messageSid) {
        try {
            ProcessedMessage pm = new ProcessedMessage();
            pm.setMessageSid(messageSid);
            repository.save(pm);
        } catch (Exception e) {
            // Race: another thread inserted the same SID between the check
            // and the insert. This is harmless — the first insert wins.
            log.debug("SID {} already marked (concurrent insert)", messageSid);
        }
    }
}
