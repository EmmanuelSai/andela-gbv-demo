package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.CaseService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WitnessContactHandler implements MenuActionHandler {
    private final CaseService caseService;

    @Override
    public String getName() {
        return "witnessContactHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        String caseId = (String) context.get("caseId");
        if (caseId == null || input == null || input.isBlank())
            return;
        caseService.saveWitnessContact(UUID.fromString(caseId), input);
    }
}
