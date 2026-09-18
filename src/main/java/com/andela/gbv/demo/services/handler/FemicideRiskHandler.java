package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.CaseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FemicideRiskHandler implements MenuActionHandler {
    private final CaseService caseService;

    @Override
    public String getName() {
        return "femicideRiskHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        String caseIdStr = (String) context.get("caseId");
        String flag = (String) context.get("_riskFlag");
        if (caseIdStr == null || flag == null)
            return;

        caseService.applyRiskFlag(UUID.fromString(caseIdStr), flag);
    }
}
