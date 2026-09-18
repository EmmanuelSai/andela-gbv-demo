package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

public record VaultEntryDto(
        UUID id,
        String blob,
        Instant createdAt) {
}
