package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.CaseService;
import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SafetyVaultHandler implements MenuActionHandler {
    private final CaseService caseService;
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "safetyVaultHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        String caseIdStr = (String) context.get("caseId");
        if (caseIdStr == null) {
            twilioService.sendWhatsAppMessage(userNumber,
                    "Please start a report first so we can attach evidence securely.");
            return;
        }
        UUID caseId = UUID.fromString(caseIdStr);

        // Encrypted payload — in a full implementation, this would include the
        // sanitized media reference. Here we store a marker plus the user's note.
        String payload = input == null ? "(media attached)" : input;
        String token = caseService.createSafetyVault(caseId, payload);

        twilioService.sendWhatsAppMessage(userNumber,
                "Your evidence has been saved securely.\n\n" +
                        "Access token (first 8 characters): " + token.substring(0, 8) + "...\n\n" +
                        "Keep this token safe. You will need it to share evidence with a lawyer or case manager.\n" +
                        "We cannot unlock your evidence without this token.");
    }
}
