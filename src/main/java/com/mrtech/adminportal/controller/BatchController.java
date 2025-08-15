package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.Batch;
import com.mrtech.adminportal.repository.BatchRepository;
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

    // ✅ Update batch status
    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateBatchStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
        Optional<Batch> optionalBatch = batchRepo.findById(id);
        if (optionalBatch.isPresent()) {
            Batch batch = optionalBatch.get();
            batch.setStatus(payload.get("status"));
            batchRepo.save(batch);
            return ResponseEntity.ok(batch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Batch not found");
    }
    
 // ✅ Get only Ongoing and Upcoming batches
    @GetMapping("/active")
    public List<Batch> getActiveBatches() {
        List<Batch> allBatches = batchRepo.findAll();
        List<Batch> filteredBatches = new ArrayList<>();

        for (Batch batch : allBatches) {
            String status = batch.getStatus();
            if ("Ongoing".equalsIgnoreCase(status) || "Upcoming".equalsIgnoreCase(status)) {
                filteredBatches.add(batch);
            }
        }

        return filteredBatches;
    }
    
    //payments table
 // ✅ Get status by batch ID (batch code)
    @GetMapping("/status/{batchid}")
    public ResponseEntity<?> getBatchStatus(@PathVariable String batchid) {
        Optional<Batch> optionalBatch = batchRepo.findById(batchid);
        if (optionalBatch.isPresent()) {
            String status = optionalBatch.get().getStatus();
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Batch not found");
        }
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
        List<Batch> batches = batchRepo.findAll();
        Set<String> statuses = new HashSet<>();

        for (Batch b : batches) {
            if (b.getCoursename().equalsIgnoreCase(coursename)) {
                statuses.add(b.getStatus());
            }
        }

        return ResponseEntity.ok(statuses);
    }
    
    

    // ✅ Get batch codes by course name and status
    @GetMapping("/codes")
    public ResponseEntity<?> getBatchCodesByCourseAndStatus(
            @RequestParam String course,
            @RequestParam String status) {

        List<Batch> batches = batchRepo.findAll();
        List<String> codes = new ArrayList<>();

        for (Batch b : batches) {
            if (b.getCoursename().equalsIgnoreCase(course) &&
                b.getStatus().equalsIgnoreCase(status)) {
                codes.add(b.getBatchid());
            }
        }

        return ResponseEntity.ok(codes);
    }



}
