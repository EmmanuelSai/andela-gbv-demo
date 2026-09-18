package com.andela.gbv.demo.dto;

import com.andela.gbv.demo.models.CaseStatus;

import jakarta.validation.constraints.NotNull;

public record CaseStatusUpdateRequest(
        @NotNull CaseStatus status) {
}
