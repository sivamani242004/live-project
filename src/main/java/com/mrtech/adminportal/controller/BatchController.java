package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.Batch;
import com.mrtech.adminportal.repository.BatchRepository;
import com.mrtech.adminportal.repository.PaymentRepository;
import com.mrtech.adminportal.repository.StudentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    // ✅ Add new batch
    @PostMapping("/add")
    public Batch addBatch(@RequestBody Batch batch) {
        return batchRepo.save(batch);
    }

    // ✅ Get all batches
    @GetMapping("/all")
    public List<Batch> getAllBatches() {
        return batchRepo.findAll();
    }

    // ✅ Get only Ongoing and Upcoming batches
    @GetMapping("/active")
    public List<Batch> getActiveBatches() {
        return batchRepo.findAll().stream()
                .filter(b -> "Ongoing".equalsIgnoreCase(b.getStatus()) || "Upcoming".equalsIgnoreCase(b.getStatus()))
                .toList();
    }

    // ✅ Get status by batch ID
    @GetMapping("/status/{batchid}")
    public ResponseEntity<?> getBatchStatus(@PathVariable String batchid) {
        return batchRepo.findById(batchid)
                .map(batch -> ResponseEntity.ok(batch.getStatus()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Batch not found"));
    }

    // ✅ Get active batches for a given course
    @GetMapping("/byCourse/{courseName}")
    public List<Batch> getBatchesByCourse(@PathVariable String courseName) {
        return batchRepo.findByCoursenameIgnoreCaseAndStatusIn(
                courseName,
                List.of("Ongoing", "Upcoming")
        );
    }

    // ✅ Get batch statuses based on course name
    @GetMapping("/statuses/{coursename}")
    public ResponseEntity<?> getStatusesByCourse(@PathVariable String coursename) {
        Set<String> statuses = new HashSet<>();
        batchRepo.findAll().forEach(b -> {
            if (b.getCoursename().equalsIgnoreCase(coursename)) {
                statuses.add(b.getStatus());
            }
        });
        return ResponseEntity.ok(statuses);
    }

    // ✅ Get batch codes by course name and status
    @GetMapping("/codes")
    public ResponseEntity<?> getBatchCodesByCourseAndStatus(
            @RequestParam String course,
            @RequestParam String status) {
        List<String> codes = new ArrayList<>();
        batchRepo.findAll().forEach(b -> {
            if (b.getCoursename().equalsIgnoreCase(course) &&
                    b.getStatus().equalsIgnoreCase(status)) {
                codes.add(b.getBatchid());
            }
        });
        return ResponseEntity.ok(codes);
    }
    
    // ✅ Update Batch + Students + Payments together
    @Transactional
    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateBatchStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        return batchRepo.findById(id).map(batch -> {
            String newStatus = payload.get("status");

            // Update batch
            batch.setStatus(newStatus);
            batchRepo.save(batch);

            // Update students
            studentRepo.updateStatusByBatch(id, newStatus);

            // Update payments
         // ✅ Bulk update payments in one query
            paymentRepo.updateStatusByBatch(batch.getBatchid(), newStatus);


            return ResponseEntity.ok("Batch, students, and payments updated successfully!");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Batch not found"));
    }
}
