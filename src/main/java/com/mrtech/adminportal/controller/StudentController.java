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
import com.mrtech.adminportal.service.FinanceReportService;
import com.mrtech.adminportal.service.StudentService;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private FinanceReportService financeReportService;


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentService studentService;

    /**
     * 🟢 Register new student + create initial payment record
     */
    @PostMapping
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        // Save student
        Student savedStudent = studentRepository.save(student);

        // Create first payment record
        Payment payment = new Payment();
        payment.setStudentId(savedStudent.getId());
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
     // ✅ Auto update Finance Report here
        financeReportService.generateMonthlyFinanceReport();
        return ResponseEntity.status(201).body(savedStudent);
    }

    /**
     * 🔍 Search students by name or mobile
     */
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(
                studentRepository.findByNameContainingIgnoreCaseOrMobileContaining(keyword, keyword)
        );
    }

    /**
     * ❌ Delete student by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        return ResponseEntity.ok(studentRepository.count());
    }


    /**
     * 🔎 Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 📋 Get all students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    /**
     * ✏️ Update student details (and sync with payment if exists)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student updatedData) {
        return studentRepository.findById(id).map(existing -> {
            // Update fields
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

            // Sync first payment record if exists
            List<Payment> payments = paymentRepository.findByStudentId(saved.getId());
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

    /**
     * 🔍 Filter students by course, status and batch
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> filterStudents(
            @RequestParam String course,
            @RequestParam String status,
            @RequestParam String batch) {
        return ResponseEntity.ok(
                studentRepository.findByCourseAndStatusAndBatch(course, status, batch)
        );
    }

    /**
     * 📊 Get total fees collected
     */
    @GetMapping("/total-fees")
    public ResponseEntity<Double> getTotalFees() {
        return ResponseEntity.ok(
                Optional.ofNullable(studentRepository.getTotalPaidAmount()).orElse(0.0)
        );
    }

    /**
     * 📆 Get upcoming due amount
     */
    @GetMapping("/upcoming-due")
    public ResponseEntity<Double> getUpcomingDue() {
        return ResponseEntity.ok(
                Optional.ofNullable(studentRepository.getTotalUpcomingDue()).orElse(0.0)
        );
    }

    /**
     * ✅ Get student with their payment details
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getStudentWithPayments(@PathVariable Integer id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (!studentOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Payment> payments = paymentRepository.findByStudentId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("student", studentOpt.get());
        response.put("payments", payments);

        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Get students by batch ID
     */
    @GetMapping("/byBatch/{batchId}")
    public ResponseEntity<List<Student>> getStudentsByBatch(@PathVariable String batchId) {
        return ResponseEntity.ok(studentRepository.findByBatch(batchId));
    }
    
    
    @GetMapping("/enrollment-trend")
    public Map<String, Object> getEnrollmentTrend() {

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(6).withDayOfMonth(1);

        DateTimeFormatter dmy = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Student> students = studentRepository.findAllStudents();

        Map<java.time.YearMonth, Long> monthlyData = new HashMap<>();

        for (Student s : students) {
            String joinDateStr = s.getJoiningDate();

            if (joinDateStr != null && !joinDateStr.isEmpty()) {
                try {
                    LocalDate joinDate;

                    // If date contains '-', detect format length
                    if (joinDateStr.matches("\\d{2}-\\d{2}-\\d{4}")) {
                        joinDate = LocalDate.parse(joinDateStr, dmy); // dd-MM-yyyy
                    } else if (joinDateStr.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                        joinDate = LocalDate.parse(joinDateStr.substring(0, 10), ymd); // yyyy-MM-dd
                    } else {
                        throw new Exception("Unknown format");
                    }

                    if (!joinDate.isBefore(startDate)) {
                        java.time.YearMonth ym = java.time.YearMonth.from(joinDate);
                        monthlyData.merge(ym, 1L, Long::sum);
                    }

                } catch (Exception e) {
                    System.err.println("⚠️ Invalid joiningDate for " + s.getName() + ": " + joinDateStr);
                }
            }
        }

        List<java.time.YearMonth> sortedMonths = new ArrayList<>(monthlyData.keySet());
        sortedMonths.sort(Comparator.naturalOrder());

        List<String> labels = sortedMonths.stream()
                .map(ym -> ym.getMonth().name().substring(0, 3) + " " + ym.getYear())
                .toList();

        List<Long> data = sortedMonths.stream()
                .map(monthlyData::get)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", labels);
        response.put("data", data);

        return response;
    }
    
    @GetMapping("/course-distribution")
    public Map<String, Object> getCourseDistribution() {
        List<Object[]> results = studentRepository.getCourseWiseCount();

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : results) {
            labels.add((String) row[0]);
            data.add((Long) row[1]);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels", labels);
        response.put("data", data);
        return response;
    }


}
