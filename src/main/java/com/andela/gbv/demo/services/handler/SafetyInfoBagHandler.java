package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SafetyInfoBagHandler implements MenuActionHandler {
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "safetyInfoBagHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        twilioService.sendWhatsAppMessage(userNumber,
                "Emergency bag checklist (keep it small and hidden):\n\n" +
                        "• ID / passport / birth certificates\n" +
                        "• Some cash\n" +
                        "• Spare phone + charger\n" +
                        "• Essential medication\n" +
                        "• A change of clothes for you and children\n" +
                        "• Keys (spare set)\n" +
                        "• A written list of safe contacts\n\n" +
                        "If possible, leave it with someone you trust.");
    }
}
