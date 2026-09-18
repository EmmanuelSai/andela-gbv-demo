package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SafetyInfoRouteHandler implements MenuActionHandler {

    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "safetyInfoRouteHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        twilioService.sendWhatsAppMessage(userNumber,
                "Planning a safe route:\n\n" +
                        "1. Choose a time when the person who hurts you is not around\n" +
                        "2. Plan the first 3 stops: where you go, who you call, how you get there\n" +
                        "3. Have a backup route in case the first is blocked\n" +
                        "4. Tell one trusted person you are leaving and when\n" +
                        "5. Bring your emergency bag if it is safe to do so\n\n" +
                        "If you can, practice the route once without going through with it.");
    }

}
