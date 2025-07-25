package com.mrtech.adminportal.repository;

import com.mrtech.adminportal.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, String> {
}
