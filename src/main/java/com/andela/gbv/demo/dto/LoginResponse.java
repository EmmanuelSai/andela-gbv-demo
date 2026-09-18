package com.andela.gbv.demo.dto;

import com.andela.gbv.demo.models.StaffRole;

public record LoginResponse(
        String token,
        StaffRole role,
        String displayName,
        String username) {
}
