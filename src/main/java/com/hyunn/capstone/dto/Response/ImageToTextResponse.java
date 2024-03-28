package com.hyunn.capstone.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ImageToTextResponse {

  @Schema(type = "String", description = "이미지", example = "human_face.img")
  private String image;

  @Schema(type = "String", description = "성별", example = "female")
  private String gender;

  @Schema(type = "String", description = "감정", example = "기쁨")
  private String emotion;

  @Schema(type = "String", description = "flask 서버에서 반환한 동물 키워드", example = "고양이")
  private List<String> keyWord;

  public ImageToTextResponse(String image, String gender, String emotion, List<String> keyWord) {
    this.image = image;
    this.gender = gender;
    this.emotion = emotion;
    this.keyWord = keyWord;
  }

  public static ImageToTextResponse create(String image, String gender, String emotion,
      List<String> keyWord) {
    return new ImageToTextResponse(image, gender, emotion, keyWord);
  }
}
