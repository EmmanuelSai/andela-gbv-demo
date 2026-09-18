package com.andela.gbv.demo.dto;

import com.andela.gbv.demo.models.StaffRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStaffUserRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(min = 8, max = 80) String password,
        @NotBlank @Size(max = 120) String displayName,
        @NotNull StaffRole role) {
}
