package com.andela.gbv.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.CaseAssignRequest;
import com.andela.gbv.demo.dto.CaseDetailDto;
import com.andela.gbv.demo.dto.CaseListItemDto;
import com.andela.gbv.demo.dto.CaseStatusUpdateRequest;
import com.andela.gbv.demo.dto.CreateReferralRequest;
import com.andela.gbv.demo.dto.PageResponse;
import com.andela.gbv.demo.dto.ReferralDto;
import com.andela.gbv.demo.dto.VaultEntryDto;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;
import com.andela.gbv.demo.security.SecurityUtils;
import com.andela.gbv.demo.services.CaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portal/cases")
@RequiredArgsConstructor
public class PortalCaseController {
    private final CaseService caseService;

    @GetMapping
    public PageResponse<CaseListItemDto> list(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) CaseType type,
            @RequestParam(required = false) FemicideRiskLevel risk,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageIndex = Math.max(page, 0);
        return caseService.listCases(status, type, risk, assignedTo, pageIndex, pageSize);
    }

    @GetMapping("/{id}")
    public CaseDetailDto get(@PathVariable UUID id) {
        return caseService.getCaseDetail(id, SecurityUtils.currentActor());
    }

    @PatchMapping("/{id}/status")
    public CaseListItemDto updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody CaseStatusUpdateRequest request) {
        return caseService.updateStatus(id, request.status(), SecurityUtils.currentActor());
    }

    @PatchMapping("/{id}/assign")
    public CaseListItemDto assign(
            @PathVariable UUID id,
            @Valid @RequestBody CaseAssignRequest request) {
        return caseService.assign(id, request.assignedTo(), SecurityUtils.currentActor());
    }

    @GetMapping("/{id}/vault")
    public List<VaultEntryDto> vault(@PathVariable UUID id) {
        return caseService.getVaultEntries(id, SecurityUtils.currentActor());
    }

    @GetMapping("/{id}/referrals")
    public List<ReferralDto> referrals(@PathVariable UUID id) {
        return caseService.listReferrals(id);
    }

    @PostMapping("/{id}/referrals")
    public ReferralDto createReferral(
            @PathVariable UUID id,
            @Valid @RequestBody CreateReferralRequest request) {
        return caseService.createReferral(
                id,
                request.serviceType(),
                request.serviceId(),
                SecurityUtils.currentActor());
    }
}
