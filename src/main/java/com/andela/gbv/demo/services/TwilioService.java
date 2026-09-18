package com.andela.gbv.demo.services;

import com.andela.gbv.demo.configs.TwilioConfig;
import com.andela.gbv.demo.utils.PiiRedactor;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioService {
    private final TwilioConfig twilioConfig;

    public TwilioService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @PostConstruct
    public void init() {
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        log.info("Twilio initialized");
    }

    public String sendWhatsAppMessage(String to, String body) {
        try {
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber(twilioConfig.getWhatsappNumber()),
                    body).create();
            log.debug("Sent to {}: SID={}", to, message.getSid());
            return message.getSid();
        } catch (Exception e) {
            log.error("Send failed to {}: {}", PiiRedactor.pseudonymize(to), e.getMessage());
            throw new RuntimeException("Message delivery failed", e);
        }
    }
}
