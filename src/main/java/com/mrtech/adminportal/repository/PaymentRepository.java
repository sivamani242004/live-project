package com.mrtech.adminportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mrtech.adminportal.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCourseTypeAndStatusDisplayAndBatchCode(String courseType, String statusDisplay, String batchCode);

    List<Payment> findByStudentId(Long studentId);  // 🔥 use int here, not Long

    List<Payment> findByPaymentDateBetweenOrderByPaymentDateDesc(LocalDate fromDate, LocalDate toDate);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :from AND :to ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsBetweenDates(@Param("from") LocalDate from, @Param("to") LocalDate to);

}
