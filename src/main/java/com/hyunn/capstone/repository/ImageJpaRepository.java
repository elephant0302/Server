package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

  List<Image> findAllByUser(Optional<User> user);

  List<Image> findAll();
}
