package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class ThreeDimensionResponse {

  private Long imageId;

  private String image;

  private String threeDimension;

  private String keyWord;

  public ThreeDimensionResponse(Long imageId, String image, String threeDimension, String keyWord) {
    this.imageId = imageId;
    this.image = image;
    this.threeDimension = threeDimension;
    this.keyWord = keyWord;
  }

  public static ThreeDimensionResponse create(Long imageId, String image, String threeDimension,
      String keyWord) {
    return new ThreeDimensionResponse(imageId, image, threeDimension, keyWord);
  }
}
