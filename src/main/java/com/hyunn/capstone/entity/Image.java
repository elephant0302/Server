package com.hyunn.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "image")
@Getter
@ToString(exclude = "imageId")
@NoArgsConstructor
public class Image extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Long imageId;

  // 등록 이미지
  @Column(name = "image")
  private String image;

  // 3D 이미지
  @Column(name = "three_dimension")
  private String threeDimension;

  // 키워드 (동물)
  @Column(name = "key_word")
  private String keyWord;

  // 감정
  @Column(name = "emotion")
  private String emotion;

  // 키워드
  @Column(name = "gender")
  private Boolean gender;

  // 조인
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private Image(String image, String threeDimension, String keyWord, String emotion, Boolean gender) {
    this.image = image;
    this.threeDimension = threeDimension;
    this.keyWord = keyWord;
    this.emotion = emotion;
    this.gender = gender;
  }

  public static Image createImage(String image, String threeDimension, String keyWord, String emotion, Boolean gender) {
    return new Image(image, threeDimension, keyWord, emotion, gender);
  }

}
