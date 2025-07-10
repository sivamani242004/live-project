package com.mrtech.adminportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrtech.adminportal.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> 
{ 
	List<Student> findByNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);
}