package com.mrtech.adminportal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Student> optionalStudent = studentRepository.findByMobile(payment.getPhoneNumber());

        if (!optionalStudent.isPresent()) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        Student student = optionalStudent.get();

        // Update paid and due amount
        double newPaidAmount = student.getPaidamount() + payment.getAmountPaid();
        double newDueFee =payment.getRemainingDue() ;

        student.setPaidamount(newPaidAmount);
        student.setDuefee(newDueFee);
        studentRepository.save(student);

        // Save payment
        Payment saved = paymentRepository.save(payment);

        return ResponseEntity.ok(saved);
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
