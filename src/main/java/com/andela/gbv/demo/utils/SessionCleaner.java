package com.andela.gbv.demo.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.configs.RateLimiter;
import com.andela.gbv.demo.services.SessionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionCleaner {
    private final SessionManager sessionManager;
    private final RateLimiter rateLimiter;

    public SessionCleaner(SessionManager sessionManager, RateLimiter rateLimiter) {
        this.sessionManager = sessionManager;
        this.rateLimiter = rateLimiter;
    }

    @Scheduled(fixedDelay = 60_000)
    public void evictExpired() {
        Instant sessionCutoff = Instant.now().minus(30, ChronoUnit.MINUTES);
        int sessions = sessionManager.evictIdleSince(sessionCutoff);
        if (sessions > 0) {
            log.info("Evicted {} idle sessions", sessions);
        }

        Instant rateCutoff = Instant.now().minus(5, ChronoUnit.MINUTES);
        int windows = rateLimiter.evictIdleSince(rateCutoff);
        if (windows > 0) {
            log.info("Evicted {} idle rate-limit windows", windows);
        }
    }
}
