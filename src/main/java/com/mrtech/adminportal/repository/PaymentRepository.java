package com.mrtech.adminportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mrtech.adminportal.entity.Payment;

import jakarta.transaction.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCourseTypeAndStatusDisplayAndBatchCode(String courseType, String statusDisplay, String batchCode);

    List<Payment> findByStudentId(long studentId);  // ✅ fixed type

    List<Payment> findByPaymentDateBetweenOrderByPaymentDateDesc(LocalDate fromDate, LocalDate toDate);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :from AND :to ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsBetweenDates(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // 🔍 Bulk update payments’ statusDisplay by batchCode
    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.statusDisplay = :status WHERE p.batchCode = :batchCode")
    int updateStatusByBatch(@Param("batchCode") String batchCode, @Param("status") String status);
}
