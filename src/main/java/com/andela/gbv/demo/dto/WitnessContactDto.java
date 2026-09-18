package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

public record WitnessContactDto(
        UUID id,
        String safeContact,
        Instant createdAt) {
}
