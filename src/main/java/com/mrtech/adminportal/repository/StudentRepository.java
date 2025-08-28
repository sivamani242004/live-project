package com.mrtech.adminportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.mrtech.adminportal.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT SUM(s.paidamount) FROM Student s") // ✅ match exactly entity field name
    Double getTotalPaidAmount();

    // 🔍 Search by name or mobile
    List<Student> findByNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);

    // Find student by ID
    Optional<Student> findById(Integer id);

    // Find student by name (case-insensitive)
    Student findByNameContainingIgnoreCase(String name);

    // Find students by course + status + batch
    List<Student> findByCourseAndStatusAndBatch(String course, String status, String batch);

    // Find by mobile
    Optional<Student> findByMobile(String mobile);

    // Find all students in a batch
    List<Student> findByBatch(String batch);

    // ✅ Bulk update student status by batch
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.status = :status WHERE s.batch = :batchId")
    int updateStatusByBatch(String batchId, String status);

 // StudentRepository.java
    @Query("SELECT SUM(s.duefee) FROM Student s WHERE s.duefee > 0")
    Double getTotalUpcomingDue();
    

	
}
