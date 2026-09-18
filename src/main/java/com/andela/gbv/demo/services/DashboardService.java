package com.andela.gbv.demo.services;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.andela.gbv.demo.dto.CaseListItemDto;
import com.andela.gbv.demo.dto.DashboardSummaryDto;
import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;
import com.andela.gbv.demo.repositories.CaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final CaseRepository caseRepository;

    public DashboardSummaryDto summary() {
        Map<CaseStatus, Long> byStatus = new EnumMap<>(CaseStatus.class);
        for (CaseStatus status : CaseStatus.values()) {
            byStatus.put(status, 0L);
        }
        for (Object[] row : caseRepository.countGroupedByStatus()) {
            byStatus.put((CaseStatus) row[0], (Long) row[1]);
        }

        Map<CaseType, Long> byType = new EnumMap<>(CaseType.class);
        for (CaseType type : CaseType.values()) {
            byType.put(type, 0L);
        }
        for (Object[] row : caseRepository.countGroupedByType()) {
            byType.put((CaseType) row[0], (Long) row[1]);
        }

        Map<FemicideRiskLevel, Long> byRisk = new EnumMap<>(FemicideRiskLevel.class);
        for (FemicideRiskLevel risk : FemicideRiskLevel.values()) {
            byRisk.put(risk, 0L);
        }
        for (Object[] row : caseRepository.countGroupedByRisk()) {
            byRisk.put((FemicideRiskLevel) row[0], (Long) row[1]);
        }

        List<CaseListItemDto> priority = caseRepository
                .findPriorityQueue(FemicideRiskLevel.IMMINENT, CaseStatus.ESCALATED, PageRequest.of(0, 10))
                .stream()
                .map(CaseListItemDto::from)
                .toList();

        return new DashboardSummaryDto(
                caseRepository.count(),
                byStatus,
                byType,
                byRisk,
                byRisk.getOrDefault(FemicideRiskLevel.IMMINENT, 0L),
                byStatus.getOrDefault(CaseStatus.ESCALATED, 0L),
                priority);
    }
}
