package com.andela.gbv.demo.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "encryption")
public class EncryptionConfig {
    @NotBlank
    private String key;
}
