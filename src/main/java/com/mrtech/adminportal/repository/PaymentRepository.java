package com.mrtech.adminportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrtech.adminportal.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
