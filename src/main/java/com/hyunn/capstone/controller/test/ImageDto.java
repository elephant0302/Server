package com.hyunn.capstone.controller.test;

import com.hyunn.capstone.entity.User;
import lombok.Getter;

@Getter
public class ImageDto {

  private Long imageId;

  private String image;

  private String threeDimension;

  private String keyWord;

  private String emotion;

  private String gender;

  private Long userId;

  public ImageDto(Long imageId, String image, String threeDimension, String keyWord,
      String emotion, String gender, Long userId) {
    this.imageId = imageId;
    this.image = image;
    this.threeDimension = threeDimension;
    this.keyWord = keyWord;
    this.emotion = emotion;
    this.gender = gender;
    this.userId = userId;
  }

  public static ImageDto create(Long imageId, String image, String threeDimension, String keyWord,
      String emotion, String gender, Long userId) {
    return new ImageDto(imageId, image, threeDimension, keyWord, emotion, gender, userId);
  }

}
