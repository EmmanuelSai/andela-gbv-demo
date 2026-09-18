package com.andela.gbv.demo.dto;

import java.util.List;
import java.util.Map;

import com.andela.gbv.demo.models.CaseStatus;
import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.FemicideRiskLevel;

public record DashboardSummaryDto(
        long totalCases,
        Map<CaseStatus, Long> byStatus,
        Map<CaseType, Long> byType,
        Map<FemicideRiskLevel, Long> byRisk,
        long imminentCount,
        long escalatedCount,
        List<CaseListItemDto> priorityQueue) {
}
