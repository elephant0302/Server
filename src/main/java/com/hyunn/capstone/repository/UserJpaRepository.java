package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByPhoneAndEmail(String phone, String email);

  boolean existsUserByPhoneAndEmail(String phone, String email);

  Optional<User> findUserByPhone(String phone);

}