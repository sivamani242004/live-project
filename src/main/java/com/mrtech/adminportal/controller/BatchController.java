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


}
