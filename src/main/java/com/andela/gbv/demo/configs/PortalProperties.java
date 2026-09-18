package com.andela.gbv.demo.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "portal")
public class PortalProperties {
    @NotBlank
    private String adminUsername = "admin";

    @NotBlank
    private String adminPassword;

    @NotBlank
    private String managerUsername = "manager";

    @NotBlank
    private String managerPassword;

    @NotBlank
    private String corsOrigins = "http://localhost:4200";

    public List<String> allowedOrigins() {
        return Arrays.stream(corsOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
