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
            Payment savedPayment = paymentRepository.save(payment);
            return ResponseEntity.ok(savedPayment);
        } catch (Exception e) {
            e.printStackTrace(); // Logs full error in console for debugging
            
            // Send the real cause in the response
            String errorMessage = e.getMessage();
            if (e.getCause() != null) {
                errorMessage = e.getCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Payment save failed: " + errorMessage);
        }
        
    }
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

    // Optional: Filtered payments by course, status, batch
    // @GetMapping("/payments/filter")
    // public List<Payment> filterPayments(@RequestParam String course,
    //                                     @RequestParam String status,
    //                                     @RequestParam String batch) {
    //     return paymentRepository.findByCourseTypeAndStatusDisplayAndBatchCode(course, status, batch);
    // }
}
