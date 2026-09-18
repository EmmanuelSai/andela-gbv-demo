package com.andela.gbv.demo.dto;

import com.andela.gbv.demo.models.ReferralStatus;

import jakarta.validation.constraints.NotNull;

public record ReferralStatusUpdateRequest(
        @NotNull ReferralStatus status) {
}
