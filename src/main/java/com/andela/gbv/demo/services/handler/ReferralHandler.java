package com.andela.gbv.demo.services.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.services.ReferralService;
import com.andela.gbv.demo.services.TwilioService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReferralHandler implements MenuActionHandler {
    private final TwilioService twilioService;
    private final ReferralService referralService;

    @Override
    public String getName() {
        return "referralHandler";
    }

    @Override
    public void handle(String userNumber, String input, Map<String, Object> context) {
        String location = (String) context.getOrDefault("_location", "OTHER");
        List<Map<String, String>> referrals = referralService.getReferrals(location, null);

        StringBuilder sb = new StringBuilder("Referral contacts for ")
                .append(location).append(":\n\n");
        for (Map<String, String> r : referrals) {
            sb.append("• ").append(r.get("name"))
                    .append(" — ").append(r.get("phone")).append("\n");
        }
        sb.append("\nIf you are in immediate danger, please call 999.");
        twilioService.sendWhatsAppMessage(userNumber, sb.toString());
    }
}
