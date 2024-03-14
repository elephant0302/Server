package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByPhoneAndPassword(String phone, String Password);

  boolean existsUserByPhoneAndPassword(String phone, String password);

}
