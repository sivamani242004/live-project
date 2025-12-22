package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.FinanceReport;
import com.mrtech.adminportal.repository.FinanceReportRepository;
import com.mrtech.adminportal.service.FinanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class FinanceReportController {

    @Autowired
    private FinanceReportService financeReportService;

    @Autowired
    private FinanceReportRepository financeReportRepository;

    // Keep old functionality: generate reports (scheduled already in service)
    @PostMapping("/generate")
    public String generateFinanceReport() {
        financeReportService.generateMonthlyFinanceReport();
        return "Finance report generated successfully!";
    }

    // existing monthly-fees endpoint (keeps it)
    @GetMapping("/monthly-fees")
    public Map<String, Object> getMonthlyFees() {
        List<FinanceReport> reports = financeReportRepository.findAll();

        Map<YearMonth, Double> monthlyData = new TreeMap<>();

        for (FinanceReport r : reports) {
            try {
                String monthName = r.getMonth(); // e.g. "January"
                int year = r.getYear();
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
    
    // NEW: Return all finance rows as JSON with numeric values (keys match frontend expectations)
    @GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllFinanceReportsAsCsvLike() {
        List<FinanceReport> reports = financeReportRepository.findAll();

        System.out.println("DEBUG: getAllFinanceReportsAsCsvLike() found " + reports.size() + " rows");

        Map<String,Integer> mIdx = new HashMap<>();
        String[] months = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
        for (int i=0;i<months.length;i++) mIdx.put(months[i], i+1);

        // defensive sort: if month not recognized, put at end using year*100 + 99
        reports.sort(Comparator.comparingInt((FinanceReport r) -> {
            String m = (r.getMonth() == null) ? "" : r.getMonth().toUpperCase().trim();
            int mnum = mIdx.getOrDefault(m, 99);
            return r.getYear() * 100 + mnum;
        }));

        List<Map<String, Object>> out = reports.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("Year", r.getYear());
            m.put("Month", r.getMonth());
            // round to 2 decimal places here to avoid floating-point tails in JSON
            m.put("Income", round(r.getIncome(), 2));
            m.put("Running Expenses", round(r.getRunningExpenses(), 2));
            m.put("Salaries", round(r.getSalaries(), 2));
            m.put("Trainer Share", round(r.getTrainerShare(), 2));
            m.put("Expenditure", round(r.getExpenditure(), 2));
            m.put("Profit/Loss", round(r.getProfitOrLoss(), 2));
            return m;
        }).collect(Collectors.toList());

        System.out.println("DEBUG: returning " + out.size() + " mapped rows");
        return out;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return tmp / (double) factor;
    }
}
