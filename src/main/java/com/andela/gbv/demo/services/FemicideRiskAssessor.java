package com.andela.gbv.demo.services;

import org.springframework.stereotype.Service;

import com.andela.gbv.demo.models.FemicideIndicators;
import com.andela.gbv.demo.models.FemicideRiskLevel;

/**
 * PoC-level rubric derived from the logic of the Campbell Danger Assessment
 * and the Lethality Assessment Program (LAP). NOT a clinical tool.
 * For a real pilot, adapt weights with a GBV specialist.
 */

@Service
public class FemicideRiskAssessor {
    public FemicideRiskLevel assess(FemicideIndicators i) {
        if (i == null)
            return FemicideRiskLevel.UNKNOWN;
        int score = 0;
        if (i.isStrangulation())
            score += 4;
        if (i.isThreatsToKill())
            score += 4;
        if (i.isWeaponUse())
            score += 3;
        if (i.isRecentSeparation())
            score += 2;
        if (i.isStalkingOrMonitoring())
            score += 2;
        if (i.isPregnancy())
            score += 2;
        if (i.isAccessToFirearms())
            score += 2;
        if (i.isThreatsToFamily())
            score += 1;
        if (i.isSubstanceAbuseByPerpetrator())
            score += 1;
        if (i.isPriorGBV())
            score += 1;

        if (score >= 6)
            return FemicideRiskLevel.IMMINENT;
        if (score >= 3)
            return FemicideRiskLevel.ELEVATED;
        return FemicideRiskLevel.LOW;
    }
}
