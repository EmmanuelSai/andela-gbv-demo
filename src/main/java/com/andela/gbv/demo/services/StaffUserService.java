package com.andela.gbv.demo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andela.gbv.demo.dto.CreateStaffUserRequest;
import com.andela.gbv.demo.dto.StaffUserDto;
import com.andela.gbv.demo.entities.StaffUser;
import com.andela.gbv.demo.models.StaffRole;
import com.andela.gbv.demo.repositories.StaffUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffUserService {
    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StaffUserDto> listUsers() {
        return staffUserRepository.findAll().stream()
                .map(StaffUserDto::from)
                .toList();
    }

    @Transactional
    public StaffUserDto create(CreateStaffUserRequest request) {
        if (staffUserRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        StaffUser user = new StaffUser();
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());
        user.setRole(request.role());
        user.setEnabled(true);
        staffUserRepository.save(user);
        return StaffUserDto.from(user);
    }

    @Transactional
    public StaffUserDto setEnabled(UUID id, boolean enabled) {
        StaffUser user = staffUserRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Staff user not found: " + id));
        user.setEnabled(enabled);
        staffUserRepository.save(user);
        return StaffUserDto.from(user);
    }

    public StaffUser requireEnabled(String username) {
        StaffUser user = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account disabled");
        }
        return user;
    }

    @Transactional
    public void ensureUser(String username, String rawPassword, String displayName, StaffRole role) {
        StaffUser user = staffUserRepository.findByUsername(username).orElseGet(StaffUser::new);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setEnabled(true);
        staffUserRepository.save(user);
    }
}
