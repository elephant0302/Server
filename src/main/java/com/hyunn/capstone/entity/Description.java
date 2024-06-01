package com.hyunn.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "description")
@Getter
@ToString(exclude = "descriptionId")
@NoArgsConstructor
public class Description {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "description_id")
  private Long descriptionId;

  // 닮은 동물 키워드
  @Column(name = "keyword")
  private String keyword;

  // 성별
  @Column(name = "gender")
  private String gender;

  // 제목
  @Column(name = "title")
  private String title;

  // 예시
  @Column(name = "example")
  private String example;

}
