package com.mrtech.adminportal.service;

import com.mrtech.adminportal.entity.FinanceReport;
import com.mrtech.adminportal.repository.FinanceReportRepository;
import com.mrtech.adminportal.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Month;
import java.util.List;

@Service
public class FinanceReportService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FinanceReportRepository financeReportRepository;

    // ✅ Auto-generate report daily at midnight
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void generateMonthlyFinanceReport() {
        System.out.println("🟢 Starting finance report generation...");
        // Step 1: Clear old data
        financeReportRepository.deleteAll();

        // Step 2: Fetch fresh data
        List<Object[]> results = paymentRepository.findMonthlyIncomeSummary();

        if (results == null || results.isEmpty()) {
            System.out.println("⚠️ No payment data found to generate report!");
            return;
        }

        // Step 3: Calculate and insert reports
        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int monthNumber = ((Number) row[1]).intValue();
            double totalIncome = ((Number) row[2]).doubleValue();

            String monthName = Month.of(monthNumber).name();
            String formattedMonth = monthName.substring(0, 1) + monthName.substring(1).toLowerCase();

            double runningExpenses = totalIncome * 0.35;  // 35% of income
            double salaries = totalIncome * 0.28;         // 28% of income
            double trainerShare = totalIncome * 0.22;     // 22% of income
            double expenditure = runningExpenses + salaries + trainerShare;
            double profitOrLoss = totalIncome - expenditure;

            // Save report (rounded values)
            FinanceReport report = new FinanceReport();
            report.setYear(year);
            report.setMonth(formattedMonth);
            report.setIncome(round(totalIncome, 2));
            report.setRunningExpenses(round(runningExpenses, 2));
            report.setSalaries(round(salaries, 2));
            report.setTrainerShare(round(trainerShare, 2));
            report.setExpenditure(round(expenditure, 2));
            report.setProfitOrLoss(round(profitOrLoss, 2));

            financeReportRepository.save(report);
        }

        // Step 4: Export to CSV
        exportFinanceDataToCsv();

        System.out.println("✅ Finance report generated & exported at " + java.time.LocalTime.now());
    }

    // rounding helper
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return tmp / (double) factor;
    }

    // ✅ Export finance data to CSV file
    private void exportFinanceDataToCsv() {
        String filePath = "src/main/resources/static/financial_data.csv";
        List<FinanceReport> reports = financeReportRepository.findAll();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Year,Month,Income,Running Expenses,Salaries,Trainer Share,Expenditure,Profit/Loss\n");

            for (FinanceReport report : reports) {
                writer.write(String.format("%d,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        report.getYear(),
                        report.getMonth(),
                        report.getIncome(),
                        report.getRunningExpenses(),
                        report.getSalaries(),
                        report.getTrainerShare(),
                        report.getExpenditure(),
                        report.getProfitOrLoss()
                ));
            }

            System.out.println("💾 Exported finance data to: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error exporting finance data: " + e.getMessage());
        }
    }
}
