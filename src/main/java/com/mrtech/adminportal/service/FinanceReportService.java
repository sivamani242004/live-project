package com.mrtech.adminportal.service;

import com.mrtech.adminportal.entity.FinanceReport;
import com.mrtech.adminportal.repository.FinanceReportRepository;
import com.mrtech.adminportal.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.List;

@Service
public class FinanceReportService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FinanceReportRepository financeReportRepository;

    // 🔁 Runs automatically every 3 seconds
    @Transactional
    @Scheduled(fixedRate = 3000)
    public void generateMonthlyFinanceReport() {
        // Step 1: Clear old data first
        financeReportRepository.deleteAll();

        // Step 2: Fetch fresh monthly income summary from payments
        List<Object[]> results = paymentRepository.findMonthlyIncomeSummary();

        if (results == null || results.isEmpty()) {
            System.out.println("⚠️ No payment data found to generate report!");
            return;
        }

        // Step 3: Insert recalculated finance report
        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int monthNumber = ((Number) row[1]).intValue();
            double totalIncome = ((Number) row[2]).doubleValue();

            String monthName = Month.of(monthNumber).name();
            String formattedMonth = monthName.substring(0, 1) + monthName.substring(1).toLowerCase();

            FinanceReport report = new FinanceReport();
            report.setYear(year);
            report.setMonth(formattedMonth);
            report.setIncome(totalIncome);

            double trainerShare = 1000;
            double expenditure = 1000;
            double salaries = 1000;

            double profitOrLoss = totalIncome - (trainerShare + expenditure + salaries);

            report.setTrainerShare(trainerShare);
            report.setExpenditure(expenditure);
            report.setSalaries(salaries);
            report.setProfitOrLoss(profitOrLoss);

            financeReportRepository.save(report);
        }

        System.out.println("✅ Finance report auto-refreshed at " + java.time.LocalTime.now());
    }
}
