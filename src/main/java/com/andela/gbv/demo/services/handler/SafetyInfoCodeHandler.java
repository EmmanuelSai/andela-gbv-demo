package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SafetyInfoCodeHandler implements MenuActionHandler {
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "safetyInfoCodeHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        twilioService.sendWhatsAppMessage(userNumber,
                "A safe word is a code you and a trusted friend agree on.\n\n" +
                        "• It should sound ordinary in a normal message\n" +
                        "• It means 'I am not safe — call for help'\n" +
                        "• Do not write it down anywhere the person who hurts you can find it\n\n" +
                        "Example: 'Can you pick up the blue shirt?' = I need help now.");
    }
}
