package com.andela.gbv.demo.dto;

import java.time.Instant;
import java.util.UUID;

import com.andela.gbv.demo.entities.StaffUser;
import com.andela.gbv.demo.models.StaffRole;

public record StaffUserDto(
        UUID id,
        String username,
        String displayName,
        StaffRole role,
        boolean enabled,
        Instant createdAt) {
    public static StaffUserDto from(StaffUser user) {
        return new StaffUserDto(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt());
    }
}
