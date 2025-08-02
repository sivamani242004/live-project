package com.mrtech.adminportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrtech.adminportal.entity.Payment;
import com.mrtech.adminportal.repository.PaymentRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow frontend requests
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/payments")
    public ResponseEntity<Payment> addPayment(@RequestBody Payment payment) {
        Payment saved = paymentRepository.save(payment);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }
    
 // Don't include 'api' again in the method mapping
    @GetMapping("/payments/filter")
    public List<Payment> filterPayments(@RequestParam String course,
                                        @RequestParam String status,
                                        @RequestParam String batch) {
        return paymentRepository.findByCourseTypeAndStatusDisplayAndBatchCode(course, status, batch);
    }



    
    
    
   
}
