package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class ThreeDimensionCreateResponse {

  private Long imageId;

  private String previewResult;

  private String keyWord;

  public ThreeDimensionCreateResponse(Long imageId, String previewResult, String keyWord) {
    this.imageId = imageId;
    this.previewResult = previewResult;
    this.keyWord = keyWord;
  }

  public static ThreeDimensionCreateResponse create(Long imageId, String previewResult, String keyWord) {
    return new ThreeDimensionCreateResponse(imageId, previewResult, keyWord);
  }
}
