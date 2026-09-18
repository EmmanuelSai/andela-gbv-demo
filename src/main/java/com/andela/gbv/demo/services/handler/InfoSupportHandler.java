package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

@Component
@RequiredArgsConstructor

public class InfoSupportHandler implements MenuActionHandler {
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "infoSupportHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        twilioService.sendWhatsAppMessage(userNumber,
                "Support lines you can call any time:\n\n" +
                        "• National GBV Hotline: 1195\n" +
                        "• Police Emergency: 999\n" +
                        "• Childline Tanzania: 116\n" +
                        "• FIDA Tanzania (legal aid): +254720987654\n" +
                        "• GVRC Nairobi (counselling): +254720123456\n\n" +
                        "You are not alone. Help is available.");
    }
}
