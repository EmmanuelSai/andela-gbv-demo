package com.andela.gbv.demo.services.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.andela.gbv.demo.models.UserSession;

@Component
public class SetLanguageSwHandler implements MenuActionHandler {
    @Override
    public String getName() {
        return "setLanguageSwHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        UserSession session = (UserSession) context.get("_session");
        if (session != null) {
            session.setLanguage("sw");
        }
    }
}
