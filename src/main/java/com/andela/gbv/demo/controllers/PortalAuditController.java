package com.andela.gbv.demo.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.AuditLogDto;
import com.andela.gbv.demo.dto.PageResponse;
import com.andela.gbv.demo.entities.AuditLog;
import com.andela.gbv.demo.repositories.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portal/audit")
@RequiredArgsConstructor
public class PortalAuditController {
    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public PageResponse<AuditLogDto> list(
            @RequestParam(required = false) UUID caseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        int pageSize = Math.min(Math.max(size, 1), 100);
        int pageIndex = Math.max(page, 0);
        Page<AuditLog> result = caseId != null
                ? auditLogRepository.findByCaseIdOrderByAtDesc(caseId, PageRequest.of(pageIndex, pageSize))
                : auditLogRepository.findAllByOrderByAtDesc(PageRequest.of(pageIndex, pageSize));
        return new PageResponse<>(
                result.getContent().stream().map(AuditLogDto::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }
}
