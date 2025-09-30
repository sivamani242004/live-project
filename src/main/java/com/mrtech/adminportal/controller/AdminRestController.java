package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.FinancialData;
import com.mrtech.adminportal.service.FinancialDataCsvService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final FinancialDataCsvService csvService;

    public AdminRestController(FinancialDataCsvService csvService) {
        this.csvService = csvService;
    }

    // API: returns JSON from CSV
    @GetMapping("/financial-data")
    public List<FinancialData> getFinancialData() {
        return csvService.loadFinancialData();
    }
}
