package com.andela.gbv.demo.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReferralService {
        private static final Map<String, List<Map<String, String>>> REFERRALS = Map.of(
                        "DARESSALAAM", List.of(
                                        Map.of("name", "GVRC Dar es Salaam", "phone", "+254720123456", "type",
                                                        "counselling"),
                                        Map.of("name", "Police Gender Desk", "phone", "999", "type", "legal"),
                                        Map.of("name", "FIDA Tanzania", "phone", "+254720987654", "type", "legal"),
                                        Map.of("name", "National GBV Hotline", "phone", "1195", "type", "hotline")),
                        "ARUSHA", List.of(
                                        Map.of("name", "Arusha GBV Center", "phone", "+254720222333", "type",
                                                        "counselling"),
                                        Map.of("name", "Police Gender Desk", "phone", "999", "type", "legal"),
                                        Map.of("name", "National GBV Hotline", "phone", "1195", "type", "hotline")),
                        "DODOMA", List.of(
                                        Map.of("name", "Dodomaa GBV Center", "phone", "+254720333444", "type",
                                                        "counselling"),
                                        Map.of("name", "Police Gender Desk", "phone", "999", "type", "legal"),
                                        Map.of("name", "National GBV Hotline", "phone", "1195", "type", "hotline")),
                        "OTHER", List.of(
                                        Map.of("name", "National GBV Hotline", "phone", "1195", "type", "hotline"),
                                        Map.of("name", "Police Emergency", "phone", "999", "type", "legal")));

        public List<Map<String, String>> getReferrals(String location, String type) {
                List<Map<String, String>> all = REFERRALS.getOrDefault(
                                location == null ? "OTHER" : location.toUpperCase(), REFERRALS.get("OTHER"));
                if (type == null || type.isBlank())
                        return all;
                return all.stream().filter(r -> r.get("type").equalsIgnoreCase(type)).toList();
        }

        public List<String> listLocations() {
                return REFERRALS.keySet().stream().sorted().toList();
        }
}
