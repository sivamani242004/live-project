package com.mrtech.adminportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mrtech.adminportal.entity.courses;

public interface coursesRepository extends JpaRepository<courses, Long> {

    @Query("SELECT COALESCE(MAX(c.courseid), 0) FROM courses c")
    Long findMaxCourseId();
}
