package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.models.CaseType;
import com.andela.gbv.demo.models.UserSession;
import com.andela.gbv.demo.services.CaseService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GbvIntakeHandler implements MenuActionHandler{
private final CaseService caseService;

    @Override public String getName() { return "gbvIntakeHandler"; }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        UserSession session = (UserSession) context.get("_session");
        String lang = session != null ? session.getLanguage() : "en";

        UUID caseId = caseService.createCase(CaseType.SURVIVOR, input, lang);
        context.put("caseId", caseId.toString());
    }
}
