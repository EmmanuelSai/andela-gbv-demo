package com.andela.gbv.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.andela.gbv.demo.models.StaffRole;
import com.andela.gbv.demo.services.StaffUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffUserSeeder implements CommandLineRunner {
    private final StaffUserService staffUserService;
    private final PortalProperties portalProperties;

    @Override
    public void run(String... args) {
        staffUserService.ensureUser(
                portalProperties.getAdminUsername(),
                portalProperties.getAdminPassword(),
                "Portal Admin",
                StaffRole.ADMIN);
        staffUserService.ensureUser(
                portalProperties.getManagerUsername(),
                portalProperties.getManagerPassword(),
                "Case Manager",
                StaffRole.CASE_MANAGER);
        log.info("Staff portal users ready (admin={}, manager={})",
                portalProperties.getAdminUsername(),
                portalProperties.getManagerUsername());
    }
}
