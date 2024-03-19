package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByPhoneAndPassword(String phone, String Password);

  boolean existsUserByPhoneAndPassword(String phone, String password);

  @Query("SELECT u FROM User u WHERE u.status = false AND u.date <= :date")
  List<User> findInactiveUsers(@Param("date") LocalDateTime date);
}
