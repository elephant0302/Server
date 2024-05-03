package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ThreeDimensionResponse {

  @Schema(type = "Long", description = "이미지 id", example = "1")
  private Long imageId;

  @Schema(type = "String", description = "이미지", example = "human_face.img")
  private String image;

  @Schema(type = "String", description = "3D obj를 받을 수 있는 Uri")
  private String threeDimension;

  @Schema(type = "String", description = "flask 서버에서 반환한 동물 키워드", example = "고양이")
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
