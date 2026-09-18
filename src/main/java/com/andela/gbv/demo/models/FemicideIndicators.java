package com.andela.gbv.demo.models;

import lombok.Data;

import java.time.Instant;

@Data
public class FemicideIndicators {
    private boolean strangulation;
    private boolean threatsToKill;
    private boolean weaponUse;
    private boolean stalkingOrMonitoring;
    private boolean recentSeparation;
    private boolean pregnancy;
    private boolean priorGBV;
    private boolean threatsToFamily;
    private boolean substanceAbuseByPerpetrator;
    private boolean accessToFirearms;
    private Instant lastUpdated = Instant.now();

    public void touch() { this.lastUpdated = Instant.now(); }
}
