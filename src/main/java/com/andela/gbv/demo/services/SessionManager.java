package com.andela.gbv.demo.services;

import org.springframework.stereotype.Service;

import com.andela.gbv.demo.models.UserSession;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {
    private final ConcurrentHashMap<String, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getOrCreate(String userNumber) {
        return sessions.computeIfAbsent(userNumber, n -> {
            UserSession s = new UserSession();
            s.setUserNumber(n);
            return s;
        });
    }

    public void save(UserSession s) {
        s.setLastActivity(Instant.now());
    }

    public void clear(String userNumber) {
        sessions.remove(userNumber);
    }

    public int evictIdleSince(Instant cutoff) {
        int before = sessions.size();
        sessions.entrySet().removeIf(e -> e.getValue().getLastActivity().isBefore(cutoff));
        return before - sessions.size();
    }
}
