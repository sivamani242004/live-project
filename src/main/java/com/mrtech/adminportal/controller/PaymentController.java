package com.mrtech.adminportal.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mrtech.adminportal.entity.Payment;
import com.mrtech.adminportal.entity.Student;
import com.mrtech.adminportal.repository.PaymentRepository;
import com.mrtech.adminportal.repository.StudentRepository;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Save new payment
    @PostMapping
    public Payment savePayment(@RequestBody Payment payment) {
        Student student = studentRepository.findById(payment.getStudentId()).orElse(null);
        if (student != null) {
            payment.setFullName(student.getName());
            payment.setPhone(student.getMobile());
        }
        payment.setDatePaid(LocalDate.now());
        return paymentRepository.save(payment);
    }

    // Get today's payments
    @GetMapping("/today")
    public List<Payment> getTodayPayments() {
        return paymentRepository.findByDatePaid(LocalDate.now());
    }

    // Get all payments for a student
    @GetMapping("/student/{studentId}")
    public List<Payment> getPaymentsByStudentId(@PathVariable int studentId) {
        return paymentRepository.findByStudentId(studentId);
    }
}
