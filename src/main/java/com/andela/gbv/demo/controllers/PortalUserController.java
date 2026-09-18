package com.andela.gbv.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.CreateStaffUserRequest;
import com.andela.gbv.demo.dto.StaffUserDto;
import com.andela.gbv.demo.services.StaffUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portal/users")
@RequiredArgsConstructor
public class PortalUserController {
    private final StaffUserService staffUserService;

    @GetMapping
    public List<StaffUserDto> list() {
        return staffUserService.listUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffUserDto create(@Valid @RequestBody CreateStaffUserRequest request) {
        return staffUserService.create(request);
    }

    @PatchMapping("/{id}/enabled")
    public StaffUserDto setEnabled(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            throw new IllegalArgumentException("enabled is required");
        }
        return staffUserService.setEnabled(id, enabled);
    }
}
