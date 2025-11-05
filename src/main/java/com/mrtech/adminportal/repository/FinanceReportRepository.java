package com.mrtech.adminportal.repository;

import com.mrtech.adminportal.entity.FinanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FinanceReportRepository extends JpaRepository<FinanceReport, Long> {
    Optional<FinanceReport> findByYearAndMonth(int year, String month);
}
