package com.example.docker_container_manager.notifier;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @Autowired
    private Report report;

    @GetMapping
    public Status getStatus() {
        return report.getStatus();
    }

    @GetMapping("/report")
    public Report getLastReport() {
        Report lastReport = report.clone();
        report.reset();
        return lastReport;
    }
}
