package com.mrtech.adminportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mrtech.adminportal.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByDatePaid(LocalDate date);
    List<Payment> findByStudentId(int studentId);
}
