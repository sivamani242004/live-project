package com.mrtech.adminportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrtech.adminportal.entity.Batch;

public interface BatchRepository extends JpaRepository<Batch, String> {
    // Fetch batches for a course where status is "Ongoing" or "Upcoming"
    List<Batch> findByCoursenameIgnoreCaseAndStatusIn(String coursename, List<String> statuses);
}
