package com.andela.gbv.demo.services.handler;

import org.springframework.stereotype.Component;
import java.util.Map;
import com.andela.gbv.demo.models.UserSession;

@Component
public class SetLanguageEnHandler implements MenuActionHandler{
@Override
    public String getName() {
        return "setLanguageEnHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        UserSession session = (UserSession) context.get("_session");
        if (session != null) {
            session.setLanguage("en");
        }
    }
}
