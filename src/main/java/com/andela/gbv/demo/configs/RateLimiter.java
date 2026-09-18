package com.andela.gbv.demo.configs;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiter {
    private static final int MAX_MESSAGES_PER_MINUTE = 20;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public boolean allow(String userNumber) {
        Window w = windows.computeIfAbsent(userNumber,
                k -> new Window(Instant.now(), 0));
        synchronized (w) {
            Instant now = Instant.now();
            if (now.isAfter(w.start.plusSeconds(60))) {
                w.start = now;
                w.count = 0;
            }
            return ++w.count <= MAX_MESSAGES_PER_MINUTE;
        }
    }

    public int evictIdleSince(Instant cutoff) {
        int before = windows.size();
        windows.entrySet().removeIf(e -> {
            synchronized (e.getValue()) {
                return e.getValue().start.isBefore(cutoff);
            }
        });
        return before - windows.size();
    }

    private static class Window {
        Instant start;
        int count;

        Window(Instant s, int c) {
            this.start = s;
            this.count = c;
        }
    }
}
