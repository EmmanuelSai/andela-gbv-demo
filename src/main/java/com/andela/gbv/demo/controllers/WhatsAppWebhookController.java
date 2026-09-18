package com.andela.gbv.demo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.andela.gbv.demo.configs.RateLimiter;
import com.andela.gbv.demo.configs.TwilioConfig;
import com.andela.gbv.demo.services.MenuEngine;
import com.andela.gbv.demo.services.MessageIdempotencyService;
import com.andela.gbv.demo.utils.PiiRedactor;
import com.twilio.security.RequestValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppWebhookController {
    private final MenuEngine menuEngine;
    private final TwilioConfig twilioConfig;
    private final MessageIdempotencyService idempotencyService;
    private final RateLimiter rateLimiter;

    public WhatsAppWebhookController(MenuEngine menuEngine, TwilioConfig twilioConfig,
            MessageIdempotencyService idempotencyService, RateLimiter rateLimiter) {
        this.menuEngine = menuEngine;
        this.twilioConfig = twilioConfig;
        this.idempotencyService = idempotencyService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleIncoming(
            @RequestParam Map<String, String> params,
            @RequestHeader(value = "X-Twilio-Signature", required = false) String signature) {

        // --- Signature validation (toggleable) ---
        if (twilioConfig.isValidateSignature()) {
            if (signature == null) {
                log.warn("Missing X-Twilio-Signature header");
                return ResponseEntity.status(403).body("Missing signature");
            }
            RequestValidator validator = new RequestValidator(twilioConfig.getAuthToken());
            if (!validator.validate(twilioConfig.getWebhookUrl(), params, signature)) {
                log.warn("Invalid Twilio signature. URL={}, params={}",
                        twilioConfig.getWebhookUrl(), params.keySet());
                return ResponseEntity.status(403).body("Invalid signature");
            }
        } else {
            log.debug("Twilio signature validation DISABLED (dev mode)");
        }

        String messageSid = params.get("MessageSid");
        if (messageSid != null && !messageSid.isBlank()) {
            if (idempotencyService.alreadyProcessed(messageSid)) {
                // Twilio retry or duplicate delivery — ack without reprocessing
                log.info("Duplicate webhook for SID {}, ignoring", messageSid);
                return ResponseEntity.ok().build();
            }
        }

        String from = params.get("From");
        String body = params.get("Body");
        if (from == null)
            return ResponseEntity.badRequest().body("Missing From");

        String userNumber = from.replace("whatsapp:", "");
        String pseudo = PiiRedactor.pseudonymize(userNumber);
        if (!rateLimiter.allow(userNumber)) {
            log.warn("Rate limit exceeded for {}", pseudo);
            return ResponseEntity.ok().build();
        }
        log.info("Incoming from {}: {}", pseudo, body == null ? "" : body.substring(0, Math.min(30, body.length())));

        try {
            menuEngine.handleInput(userNumber, body != null ? body : "");
            if (messageSid != null && !messageSid.isBlank()) {
                idempotencyService.markProcessed(messageSid);
            }
        } catch (Exception e) {
            log.error("Processing error for {}: {}", pseudo, e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unused")
    private List<MediaRef> extractMediaRefs(Map<String, String> params) {
        List<MediaRef> refs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String url = params.get("MediaUrl" + i);
            if (url == null)
                break;
            refs.add(new MediaRef(url, params.get("MediaContentType" + i)));
        }
        return refs;
    }

    public record MediaRef(String url, String contentType) {
    }
}
