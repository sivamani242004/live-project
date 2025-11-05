package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.FinanceReport;
import com.mrtech.adminportal.repository.FinanceReportRepository;
import com.mrtech.adminportal.service.FinanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class FinanceReportController {

    @Autowired
    private FinanceReportService financeReportService;

    @Autowired
    private FinanceReportRepository financeReportRepository;

    // ✅ Keep old functionality
    @PostMapping("/generate")
    public String generateFinanceReport() {
        financeReportService.generateMonthlyFinanceReport();
        return "Finance report generated successfully!";
    }

    // ✅ New API for Dashboard (latest 7 months)
    @GetMapping("/monthly-fees")
    public Map<String, Object> getMonthlyFees() {
        // Fetch all finance reports
        List<FinanceReport> reports = financeReportRepository.findAll();

        // Use TreeMap to keep keys sorted alphabetically by month
        Map<String, Double> monthlyData = new LinkedHashMap<>();

        // ✅ Group income by month (String value like "September", "October", etc.)
        for (FinanceReport r : reports) {
            String key = r.getMonth().trim(); // Directly use the stored text
            monthlyData.merge(key, r.getIncome(), Double::sum);
        }

        // ✅ Convert to list and keep only the latest 7 entries
        List<String> allMonths = new ArrayList<>(monthlyData.keySet());
        int size = allMonths.size();
        List<String> last7Months = allMonths.subList(Math.max(size - 7, 0), size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", last7Months);
        response.put("data", last7Months.stream().map(monthlyData::get).toList());
        return response;
    }
}
