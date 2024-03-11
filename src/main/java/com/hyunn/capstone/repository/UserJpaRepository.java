package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {


}
