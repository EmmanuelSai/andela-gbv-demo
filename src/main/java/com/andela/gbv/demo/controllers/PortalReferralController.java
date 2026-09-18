package com.andela.gbv.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.ReferralDto;
import com.andela.gbv.demo.dto.ReferralStatusUpdateRequest;
import com.andela.gbv.demo.security.SecurityUtils;
import com.andela.gbv.demo.services.CaseService;
import com.andela.gbv.demo.services.ReferralService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portal/referrals")
@RequiredArgsConstructor
public class PortalReferralController {
    private final ReferralService referralService;
    private final CaseService caseService;

    @GetMapping("/directory")
    public List<Map<String, String>> directory(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type) {
        return referralService.getReferrals(location, type);
    }

    @GetMapping("/locations")
    public List<String> locations() {
        return referralService.listLocations();
    }

    @PatchMapping("/{id}/status")
    public ReferralDto updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ReferralStatusUpdateRequest request) {
        return caseService.updateReferralStatus(id, request.status(), SecurityUtils.currentActor());
    }
}
