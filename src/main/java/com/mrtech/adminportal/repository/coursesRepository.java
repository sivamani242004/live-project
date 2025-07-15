package com.mrtech.adminportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrtech.adminportal.entity.courses;


public interface coursesRepository extends JpaRepository<courses, String> {
}
