package com.mrtech.adminportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mrtech.adminportal.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCourseTypeAndStatusDisplayAndBatchCode(String courseType, String statusDisplay, String batchCode);

    // ✅ Fetch payments for a specific student
    List<Payment> findByStudentId(Long studentId);

    // Or use this if you want sorted by newest first
    // List<Payment> findByStudentIdOrderByPaymentDateDesc(Long studentId);
}
