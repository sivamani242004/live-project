package com.mrtech.adminportal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrtech.adminportal.entity.Payment;
import com.mrtech.adminportal.entity.Student;
import com.mrtech.adminportal.repository.PaymentRepository;
import com.mrtech.adminportal.repository.StudentRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

 // ✅ Save payment and update student record
    @PostMapping("/payments")
    public ResponseEntity<?> savePayment(@RequestBody Payment payment) {
        try {
            // 1. Save payment first
            Payment savedPayment = paymentRepository.save(payment);

            // 2. Fetch student record
            Optional<Student> studentOpt = studentRepository.findById(payment.getStudentId());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();

                // ✅ Update student financials
                double prevPaid = student.getPaidamount() != 0 ? student.getPaidamount() : 0.0;
                double newPaid = prevPaid + payment.getAmountPaid();

                student.setPaidamount(newPaid);                      // Update total paid
                student.setDuefee(student.getTotalfee() - newPaid);  // Remaining due

                // ✅ Save updated student back to DB
                studentRepository.save(student);
            }

            return ResponseEntity.ok(savedPayment);
        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = e.getMessage();
            if (e.getCause() != null) {
                errorMessage = e.getCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Payment save failed: " + errorMessage);
        }
    }

    // ✅ Get payments by student
    @GetMapping("/payments/student/{studentId}")
    public ResponseEntity<?> getPaymentsByStudent(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(paymentRepository.findByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching payment details: " + e.getMessage());
        }
    }

    // ✅ Get all payments
    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }
}
