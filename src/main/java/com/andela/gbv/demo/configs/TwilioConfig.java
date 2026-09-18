package com.andela.gbv.demo.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {
    @NotBlank
    private String accountSid;
    @NotBlank
    private String authToken;
    @NotBlank
    private String whatsappNumber;
    @NotBlank
    private String webhookUrl;
    private boolean validateSignature = true;
}
