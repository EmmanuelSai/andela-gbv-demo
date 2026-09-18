package com.andela.gbv.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andela.gbv.demo.dto.DashboardSummaryDto;
import com.andela.gbv.demo.services.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portal/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDto summary() {
        return dashboardService.summary();
    }
}
