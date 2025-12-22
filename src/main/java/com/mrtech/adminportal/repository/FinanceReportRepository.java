package com.mrtech.adminportal.repository;

import com.mrtech.adminportal.entity.FinanceReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceReportRepository extends JpaRepository<FinanceReport, Long> {
    Optional<FinanceReport> findByYearAndMonth(int year, String month);
    // Removed findByMonthAfter(LocalDate) — it compared a string column to a date.
    // If you need date-based queries, store a proper date column (e.g. year_month_date).
}
