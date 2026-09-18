package com.andela.gbv.demo.services.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.andela.gbv.demo.configs.TwilioConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    private final TwilioConfig twilioConfig;
    private final MetadataSanitizer sanitizer;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public MetadataSanitizer.SanitizedMedia fetchAndSanitize(
            String mediaUrl, String declaredContentType) {
        byte[] raw = download(mediaUrl);
        try {
            return sanitizer.sanitize(raw, declaredContentType);
        } finally {
            java.util.Arrays.fill(raw, (byte) 0);
        }
    }

    private byte[] download(String mediaUrl) {
        String auth = twilioConfig.getAccountSid() + ":" + twilioConfig.getAuthToken();
        String basic = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mediaUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + basic)
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new MetadataSanitizer.MediaSanitizationException(
                        "Twilio media fetch failed: HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MetadataSanitizer.MediaSanitizationException("Fetch error", e);
        }
    }
}
