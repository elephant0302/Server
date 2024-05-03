package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ThreeDimensionCreateResponse {

  @Schema(type = "Long", description = "이미지 id", example = "1")
  private Long imageId;

  @Schema(type = "String", description = "meshy AI에서 반환한 3D 모델 코드")
  private String previewResult;

  @Schema(type = "String", description = "flask 서버에서 반환한 동물 키워드", example = "고양이")
  private String keyWord;

  public ThreeDimensionCreateResponse(Long imageId, String previewResult, String keyWord) {
    this.imageId = imageId;
    this.previewResult = previewResult;
    this.keyWord = keyWord;
  }

  public static ThreeDimensionCreateResponse create(Long imageId, String previewResult,
      String keyWord) {
    return new ThreeDimensionCreateResponse(imageId, previewResult, keyWord);
  }
}
