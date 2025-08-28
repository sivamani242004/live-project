package com.mrtech.adminportal.controller;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrtech.adminportal.entity.Payment;
import com.mrtech.adminportal.entity.Student;
import com.mrtech.adminportal.repository.PaymentRepository;
import com.mrtech.adminportal.repository.StudentRepository;
import com.mrtech.adminportal.service.StudentService;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentService studentService;

    // 🟢 Register student + create initial payment
    @PostMapping
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        Student savedStudent = studentRepository.save(student);

        // Create first payment entry
        Payment payment = new Payment();
        payment.setStudentId(savedStudent.getId()); // ✅ use Integer directly
        payment.setStudentName(savedStudent.getName());
        payment.setCourseType(savedStudent.getCourse());
        payment.setBatchCode(savedStudent.getBatch());
        payment.setPhoneNumber(savedStudent.getMobile());

        double courseFee;
        try {
            courseFee = Double.parseDouble(savedStudent.getCoursefee());
        } catch (Exception e) {
            courseFee = savedStudent.getTotalfee();
        }

        payment.setTotalfee(courseFee);
        payment.setAmountPaid(savedStudent.getTerm_1());
        payment.setRemainingDue(savedStudent.getDuefee());
        payment.setTotalDue(courseFee - savedStudent.getPaidamount());
        payment.setPaymentDate(LocalDate.now());
        payment.setTerm("Term-1");
        payment.setStatusDisplay(savedStudent.getStatus());

        paymentRepository.save(payment);

        return ResponseEntity.status(201).body(savedStudent);
    }

    // 🔍 Search students
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(
                studentRepository.findByNameContainingIgnoreCaseOrMobileContaining(keyword, keyword)
        );
    }

    // ❌ Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Find by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 📋 Get all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    // ✏️ Update student (and sync payments if needed)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student updatedData) {
        return studentRepository.findById(id).map(existing -> {
            existing.setName(updatedData.getName());
            existing.setEmail(updatedData.getEmail());
            existing.setMobile(updatedData.getMobile());
            existing.setDob(updatedData.getDob());
            existing.setGender(updatedData.getGender());
            existing.setAddress(updatedData.getAddress());
            existing.setQualification(updatedData.getQualification());
            existing.setCourse(updatedData.getCourse());
            existing.setDuration(updatedData.getDuration());
            existing.setJoiningDate(updatedData.getJoiningDate());
            existing.setBatch(updatedData.getBatch());
            existing.setStatus(updatedData.getStatus());
            existing.setCoursefee(updatedData.getCoursefee());
            existing.setDiscount(updatedData.getDiscount());
            existing.setTotalfee(updatedData.getTotalfee());
            existing.setTerm_1(updatedData.getTerm_1());
            existing.setDuefee(updatedData.getDuefee());
            existing.setPaidamount(updatedData.getPaidamount());

            Student saved = studentRepository.save(existing);

            // Sync first payment if exists
            List<Payment> payments = paymentRepository.findByStudentId(saved.getId()); // ✅ fixed type
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);

                double courseFee;
                try {
                    courseFee = Double.parseDouble(saved.getCoursefee());
                } catch (Exception e) {
                    courseFee = saved.getTotalfee();
                }

                payment.setStudentName(saved.getName());
                payment.setCourseType(saved.getCourse());
                payment.setBatchCode(saved.getBatch());
                payment.setPhoneNumber(saved.getMobile());
                payment.setTotalfee(courseFee);
                payment.setAmountPaid(saved.getPaidamount());
                payment.setRemainingDue(saved.getDuefee());
                payment.setTotalDue(courseFee - saved.getPaidamount());
                payment.setStatusDisplay(saved.getStatus());

                paymentRepository.save(payment);
            }

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔍 Filter students
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> filterStudents(
            @RequestParam String course,
            @RequestParam String status,
            @RequestParam String batch) {
        return ResponseEntity.ok(
                studentRepository.findByCourseAndStatusAndBatch(course, status, batch)
        );
    }

    // 📊 Get total fees collected
    @GetMapping("/total-fees")
    public ResponseEntity<Double> getTotalFees() {
        return ResponseEntity.ok(
                Optional.ofNullable(studentRepository.getTotalPaidAmount()).orElse(0.0)
        );
    }

    // 📆 Get upcoming due amount
    @GetMapping("/upcoming-due")
    public ResponseEntity<Double> getUpcomingDue() {
        return ResponseEntity.ok(
                Optional.ofNullable(studentRepository.getTotalUpcomingDue()).orElse(0.0)
        );
    }

    // ✅ Student + Payment details
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getStudentWithPayments(@PathVariable Integer id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (!studentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Payment> payments = paymentRepository.findByStudentId(id); // ✅ works now

        Map<String, Object> response = new HashMap<>();
        response.put("student", studentOpt.get());
        response.put("payments", payments);

        return ResponseEntity.ok(response);
    }
 // 📌 Get students by batch
    @GetMapping("/byBatch/{batchId}")
    public ResponseEntity<List<Student>> getStudentsByBatch(@PathVariable String batchId) {
        List<Student> students = studentRepository.findByBatch(batchId);
        return ResponseEntity.ok(students);
    }

    
}
