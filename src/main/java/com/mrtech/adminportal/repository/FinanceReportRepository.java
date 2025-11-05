package com.mrtech.adminportal.repository;

import com.mrtech.adminportal.entity.FinanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mrtech.adminportal.entity.FinanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinanceReportRepository extends JpaRepository<FinanceReport, Long> {
    Optional<FinanceReport> findByYearAndMonth(int year, String month);
    
    @Query("SELECT f FROM FinanceReport f WHERE f.month >= :startDate ORDER BY f.month ASC")
    List<FinanceReport> findByMonthAfter(LocalDate startDate);
}
