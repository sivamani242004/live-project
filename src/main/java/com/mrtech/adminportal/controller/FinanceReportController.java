package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.service.FinanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceReportController {

    @Autowired
    private FinanceReportService financeReportService;

    @PostMapping("/generate")
    public String generateFinanceReport() {
        financeReportService.generateMonthlyFinanceReport();
        return "Finance report generated successfully!";
    }
}
