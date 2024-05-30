package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByTid(String tid);
}
