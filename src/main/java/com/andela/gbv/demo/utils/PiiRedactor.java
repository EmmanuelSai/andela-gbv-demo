package com.andela.gbv.demo.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class PiiRedactor {
    private static final String SALT = System.getenv().getOrDefault(
            "LOG_HASH_SALT", "w3FUk5MfAwc/O2Z9dgpiU7UzKnFPkvSM0LgK8zwGuag=");

    private PiiRedactor() {
    }

    /** Returns a stable 12-char pseudonym for log correlation. Not reversible. */
    public static String pseudonymize(String phone) {
        if (phone == null)
            return "null";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(SALT.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest(phone.getBytes(StandardCharsets.UTF_8));
            return "u_" + HexFormat.of().formatHex(hash).substring(0, 12);
        } catch (Exception e) {
            return "u_unknown";
        }
    }

    /** Masks a phone number for display: +255655512796 -> +2556*****96 */
    public static String mask(String phone) {
        if (phone == null || phone.length() < 6)
            return "***";
        return phone.substring(0, 5) + "*****" + phone.substring(phone.length() - 2);
    }
}
