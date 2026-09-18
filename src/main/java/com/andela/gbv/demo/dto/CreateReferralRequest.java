package com.andela.gbv.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReferralRequest(
        @NotBlank String serviceType,
        @NotBlank String serviceId) {
}
