package com.hyunn.capstone.repository;

import com.hyunn.capstone.entity.Description;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionJpaRepository extends JpaRepository<Description, Long> {

  Optional<Description> findByKeywordAndGender(String keyword, String gender);

}