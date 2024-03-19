package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class ThreeDimensionCreateResponse {

  private Long imageId;

  private String previewResult;

  private String prompt;

  public ThreeDimensionCreateResponse(Long imageId, String previewResult, String prompt) {
    this.imageId = imageId;
    this.previewResult = previewResult;
    this.prompt = prompt;
  }

  public static ThreeDimensionCreateResponse create(Long imageId, String previewResult, String prompt) {
    return new ThreeDimensionCreateResponse(imageId, previewResult, prompt);
  }
}
