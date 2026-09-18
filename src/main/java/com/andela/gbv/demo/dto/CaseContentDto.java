package com.andela.gbv.demo.dto;

import java.util.UUID;

public record CaseContentDto(
        UUID id,
        String text,
        String language) {
}
