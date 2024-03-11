package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {


}
