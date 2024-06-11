package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.Payment;
import com.hyunn.capstone.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {


  List<Payment> findAllByUser(Optional<User> user);

  List<Payment> findAllByTid(String tid);

  Optional<Payment> findTopByImageOrderByDateDesc(Image image);

  Boolean existsByImage(Image image);
}