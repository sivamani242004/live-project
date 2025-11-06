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
        List<FinanceReport> reports = financeReportRepository.findAll();

        Map<YearMonth, Double> monthlyData = new TreeMap<>();

        for (FinanceReport r : reports) {
            try {
                String monthName = r.getMonth(); // January
                int year = r.getYear();         // 2025

                YearMonth ym = YearMonth.of(year, java.time.Month.valueOf(monthName.toUpperCase()));

                monthlyData.merge(ym, r.getIncome(), Double::sum);
            } catch (Exception e) {
                System.out.println("⚠️ Invalid month in DB: " + r.getMonth());
            }
        }

        List<YearMonth> sorted = new ArrayList<>(monthlyData.keySet());
        int size = sorted.size();
        List<YearMonth> last7 = sorted.subList(Math.max(size - 7, 0), size);

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (YearMonth ym : last7) {
            labels.add(ym.getMonth().name().substring(0, 1).toUpperCase() + ym.getMonth().name().substring(1).toLowerCase());
            values.add(monthlyData.get(ym));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", labels);
        response.put("data", values);

        return response;
    }

}
