package com.mrtech.adminportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.mrtech.adminportal.entity.Payment;
import com.mrtech.adminportal.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);

    Optional<Student> findById(Long id);

    Student findByNameContainingIgnoreCase(String name);

    // ✅ Corrected method for String fields
  //  List<Student> findByCourseAndBatchAndDuefee(String course, String batch, String duefee);

    List<Student> findByCourseAndStatusAndBatch(String course, String status, String batch);

    // If you need status filtering (but there's no 'statusDisplay' in Student entity),
    // You must first add a field 'statusDisplay' to Student OR remove it from query.
}