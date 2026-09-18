package com.andela.gbv.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record CaseAssignRequest(
        @NotBlank String assignedTo) {
}
