package com.mrtech.adminportal.service;
import com.mrtech.adminportal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

	@Service
	public class StudentService {

	    @Autowired
	    private StudentRepository studentRepository;

	    public long getTotalStudents() {
	        return studentRepository.count();
	    }
	}


