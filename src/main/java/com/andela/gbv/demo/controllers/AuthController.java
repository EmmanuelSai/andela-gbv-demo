package com.andela.gbv.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.LoginRequest;
import com.andela.gbv.demo.dto.LoginResponse;
import com.andela.gbv.demo.entities.StaffUser;
import com.andela.gbv.demo.security.JwtService;
import com.andela.gbv.demo.services.StaffUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final StaffUserService staffUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping("/me")
    public LoginResponse me() {
        var principal = com.andela.gbv.demo.security.SecurityUtils.requirePrincipal();
        return new LoginResponse(null, principal.role(), principal.displayName(), principal.username());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        StaffUser user;
        try {
            user = staffUserService.requireEnabled(request.username());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid credentials");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        String token = jwtService.createToken(user);
        return new LoginResponse(token, user.getRole(), user.getDisplayName(), user.getUsername());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
