package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.TwilioService;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor

public class HumanConnectHandler implements MenuActionHandler {
    private final TwilioService twilioService;

    @Override
    public String getName() {
        return "humanConnectHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        // In production, this pushes to a case manager queue
        log.info("Human connect requested by {}", userNumber);
        twilioService.sendWhatsAppMessage(userNumber,
                "A trained case manager will reach out to you as soon as possible.\n\n" +
                        "If you are in immediate danger, please call 999 now.");
    }
}
