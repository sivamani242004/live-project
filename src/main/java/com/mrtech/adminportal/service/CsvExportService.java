package com.mrtech.adminportal.service;

import com.mrtech.adminportal.entity.FinanceReport;
import com.mrtech.adminportal.repository.FinanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvExportService {

    @Autowired
    private FinanceReportRepository financeReportRepository;

    // Path to your CSV file
    private static final String CSV_PATH = "src/main/resources/static/financial_data.csv";

    public void exportFinanceDataToCsv() {
        List<FinanceReport> reports = financeReportRepository.findAll();

        try (FileWriter writer = new FileWriter(CSV_PATH)) {
            // ✅ Correct header order
            writer.write("Year,Month,Income,Running Expenses,Salaries,Trainer Share,Expenditure,Profit/Loss\n");

            // ✅ Correct column mapping order
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

            System.out.println("✅ Finance data successfully exported to: " + CSV_PATH);
        } catch (IOException e) {
            System.err.println("❌ Error exporting finance data: " + e.getMessage());
        }
    }
}