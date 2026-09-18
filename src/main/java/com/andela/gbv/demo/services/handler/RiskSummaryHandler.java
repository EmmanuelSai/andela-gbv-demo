package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

/**
 * Sends a survivor-safe acknowledgment. Never exposes a risk score.
 * If IMMINENT, the case has already been silently escalated.
 */
@Component
@RequiredArgsConstructor
public class RiskSummaryHandler implements MenuActionHandler {
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "riskSummaryHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        twilioService.sendWhatsAppMessage(userNumber,
                "Thank you for answering. What you shared helps connect you with the right support.\n\n" +
                        "You can continue at your own pace. Nothing you share will be shown to the person who hurt you.");
    }
}
