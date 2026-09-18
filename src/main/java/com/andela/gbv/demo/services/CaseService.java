package com.andela.gbv.demo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andela.gbv.demo.dto.CaseContentDto;
import com.andela.gbv.demo.dto.CaseDetailDto;
import com.andela.gbv.demo.dto.CaseListItemDto;
import com.andela.gbv.demo.dto.MediaMetadataDto;
import com.andela.gbv.demo.dto.PageResponse;
import com.andela.gbv.demo.dto.ReferralDto;
import com.andela.gbv.demo.dto.VaultEntryDto;
import com.andela.gbv.demo.dto.WitnessContactDto;
import com.andela.gbv.demo.entities.Case;
import com.andela.gbv.demo.entities.CaseContent;
import com.andela.gbv.demo.entities.Referral;
import com.andela.gbv.demo.entities.SafetyVault;
import com.andela.gbv.demo.entities.WitnessContact;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideIndicators;
import com.andela.gbv.demo.models.FemicideRiskLevel;
import com.andela.gbv.demo.models.ReferralStatus;
import com.andela.gbv.demo.repositories.CaseContentRepository;
import com.andela.gbv.demo.repositories.CaseRepository;
import com.andela.gbv.demo.repositories.MediaAssetRepository;
import com.andela.gbv.demo.repositories.ReferralRepository;
import com.andela.gbv.demo.repositories.SafetyVaultRepository;
import com.andela.gbv.demo.repositories.WitnessContactRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseContentRepository caseContentRepository;
    private final SafetyVaultRepository safetyVaultRepository;
    private final ReferralRepository referralRepository;
    private final WitnessContactRepository witnessContactRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final FemicideRiskAssessor riskAssessor;
    private final AuditService auditService;

    @Transactional
    public UUID createCase(CaseType type, String content, String language) {
        Case c = new Case();
        c.setType(type);
        c.setStatus(CaseStatus.NEW);
        c.setRiskIndicators(new FemicideIndicators());
        caseRepository.save(c);

        CaseContent cc = new CaseContent();
        cc.setCaseId(c.getId());
        cc.setEncryptedText(content);
        cc.setLanguage(language);
        caseContentRepository.save(cc);

        auditService.record("bot", "CASE_CREATED", c.getId(), "type=" + type);
        log.info("Created case {} ({})", c.getId(), type);
        return c.getId();
    }

    @Transactional
    public FemicideRiskLevel applyRiskFlag(UUID caseId, String flagName) {
        Case c = requireCase(caseId);
        FemicideIndicators ind = c.getRiskIndicators();
        if (ind == null) {
            ind = new FemicideIndicators();
        }
        applyFlag(ind, flagName);
        ind.touch();

        FemicideRiskLevel level = riskAssessor.assess(ind);
        c.setRiskIndicators(ind);
        c.setFemicideRisk(level);
        if (level == FemicideRiskLevel.IMMINENT && c.getStatus() != CaseStatus.CLOSED) {
            c.setStatus(CaseStatus.ESCALATED);
            auditService.record("bot", "ESCALATED", caseId, "IMMINENT femicide risk");
            log.warn("SILENT ESCALATION: case {} flagged IMMINENT femicide risk", caseId);
        }
        caseRepository.save(c);
        return level;
    }

    @Transactional
    public void setIndicator(UUID caseId, String flagName) {
        applyRiskFlag(caseId, flagName);
    }

    public FemicideIndicators getIndicators(UUID caseId) {
        return caseRepository.findById(caseId)
                .map(Case::getRiskIndicators)
                .orElseGet(FemicideIndicators::new);
    }

    @Transactional
    public FemicideRiskLevel updateRisk(UUID caseId, FemicideRiskLevel level, FemicideIndicators ind) {
        Case c = requireCase(caseId);
        c.setFemicideRisk(level);
        c.setRiskIndicators(ind);
        caseRepository.save(c);
        log.info("Case {} risk level -> {}", caseId, level);
        return level;
    }

    @Transactional
    public void escalateToSpecialist(UUID caseId) {
        Case c = requireCase(caseId);
        c.setStatus(CaseStatus.ESCALATED);
        caseRepository.save(c);
        auditService.record("bot", "ESCALATED", caseId, "manual escalation");
        log.warn("SILENT ESCALATION: case {} flagged IMMINENT femicide risk", caseId);
    }

    @Transactional
    public void saveWitnessContact(UUID caseId, String safeContact) {
        WitnessContact wc = new WitnessContact();
        wc.setCaseId(caseId);
        wc.setSafeContact(safeContact);
        witnessContactRepository.save(wc);
    }

    @Transactional
    public String createSafetyVault(UUID caseId, String encryptedPayload) {
        String token = UUID.randomUUID() + "-" + UUID.randomUUID();
        String tokenHash = sha256Hex(token);

        SafetyVault v = new SafetyVault();
        v.setCaseId(caseId);
        v.setEncryptedBlob(encryptedPayload);
        v.setAccessTokenHash(tokenHash);
        safetyVaultRepository.save(v);

        return token;
    }

    @Transactional
    public ReferralDto createReferral(UUID caseId, String serviceType, String serviceId, String actor) {
        requireCase(caseId);
        Referral r = new Referral();
        r.setCaseId(caseId);
        r.setServiceType(serviceType);
        r.setServiceId(serviceId);
        r.setStatus(ReferralStatus.PENDING);
        referralRepository.save(r);
        auditService.record(actor, "CASE_UPDATED", caseId, "referral created: " + serviceType);
        return ReferralDto.from(r);
    }

    @Transactional
    public ReferralDto updateReferralStatus(UUID referralId, ReferralStatus status, String actor) {
        Referral r = referralRepository.findById(referralId)
                .orElseThrow(() -> new IllegalStateException("Referral not found: " + referralId));
        r.setStatus(status);
        referralRepository.save(r);
        auditService.record(actor, "CASE_UPDATED", r.getCaseId(), "referral status=" + status);
        return ReferralDto.from(r);
    }

    public FemicideRiskLevel getRiskLevel(UUID caseId) {
        return caseRepository.findById(caseId)
                .map(Case::getFemicideRisk)
                .orElse(FemicideRiskLevel.UNKNOWN);
    }

    public PageResponse<CaseListItemDto> listCases(
            CaseStatus status,
            CaseType type,
            FemicideRiskLevel risk,
            String assignedTo,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Case> result = caseRepository.findAll(
                CaseSpecifications.withFilters(status, type, risk, assignedTo),
                pageable);
        List<CaseListItemDto> content = result.getContent().stream()
                .map(CaseListItemDto::from)
                .toList();
        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Transactional
    public CaseDetailDto getCaseDetail(UUID caseId, String actor) {
        Case c = requireCase(caseId);
        List<CaseContentDto> contents = caseContentRepository.findByCaseId(caseId).stream()
                .map(cc -> new CaseContentDto(cc.getId(), cc.getEncryptedText(), cc.getLanguage()))
                .toList();
        List<WitnessContactDto> contacts = witnessContactRepository.findByCaseId(caseId).stream()
                .map(wc -> new WitnessContactDto(wc.getId(), wc.getSafeContact(), wc.getCreatedAt()))
                .toList();
        List<ReferralDto> referrals = referralRepository.findByCaseId(caseId).stream()
                .map(ReferralDto::from)
                .toList();
        List<MediaMetadataDto> media = mediaAssetRepository.findMetadataByCaseId(caseId).stream()
                .map(MediaMetadataDto::from)
                .toList();
        int vaultCount = (int) safetyVaultRepository.countByCaseId(caseId);
        auditService.record(actor, "CASE_READ", caseId, null);
        return new CaseDetailDto(
                c.getId(),
                c.getType(),
                c.getStatus(),
                c.getFemicideRisk(),
                c.getRiskIndicators(),
                c.getAssignedTo(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                contents,
                contacts,
                referrals,
                media,
                vaultCount);
    }

    @Transactional
    public CaseListItemDto updateStatus(UUID caseId, CaseStatus status, String actor) {
        Case c = requireCase(caseId);
        CaseStatus previous = c.getStatus();
        c.setStatus(status);
        caseRepository.save(c);
        auditService.record(actor, "CASE_UPDATED", caseId, "status " + previous + " -> " + status);
        return CaseListItemDto.from(c);
    }

    @Transactional
    public CaseListItemDto assign(UUID caseId, String assignedTo, String actor) {
        Case c = requireCase(caseId);
        c.setAssignedTo(assignedTo);
        if (c.getStatus() == CaseStatus.NEW) {
            c.setStatus(CaseStatus.UNDER_REVIEW);
        }
        caseRepository.save(c);
        auditService.record(actor, "CASE_UPDATED", caseId, "assigned to " + assignedTo);
        return CaseListItemDto.from(c);
    }

    @Transactional
    public List<VaultEntryDto> getVaultEntries(UUID caseId, String actor) {
        requireCase(caseId);
        List<VaultEntryDto> entries = safetyVaultRepository.findByCaseId(caseId).stream()
                .map(v -> new VaultEntryDto(v.getId(), v.getEncryptedBlob(), v.getCreatedAt()))
                .toList();
        auditService.record(actor, "VAULT_ACCESS", caseId, "entries=" + entries.size());
        return entries;
    }

    public List<ReferralDto> listReferrals(UUID caseId) {
        requireCase(caseId);
        return referralRepository.findByCaseId(caseId).stream()
                .map(ReferralDto::from)
                .toList();
    }

    private Case requireCase(UUID caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalStateException("Case not found: " + caseId));
    }

    private void applyFlag(FemicideIndicators ind, String flagName) {
        switch (flagName) {
            case "strangulation" -> ind.setStrangulation(true);
            case "threatsToKill" -> ind.setThreatsToKill(true);
            case "weaponUse" -> ind.setWeaponUse(true);
            case "recentSeparation" -> ind.setRecentSeparation(true);
            case "stalkingOrMonitoring" -> ind.setStalkingOrMonitoring(true);
            case "pregnancy" -> ind.setPregnancy(true);
            case "priorGBV" -> ind.setPriorGBV(true);
            case "threatsToFamily" -> ind.setThreatsToFamily(true);
            case "substanceAbuse" -> ind.setSubstanceAbuseByPerpetrator(true);
            case "accessToFirearms" -> ind.setAccessToFirearms(true);
            default -> log.warn("Unknown risk flag: {}", flagName);
        }
    }

    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return java.util.HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 failed", e);
        }
    }
}
