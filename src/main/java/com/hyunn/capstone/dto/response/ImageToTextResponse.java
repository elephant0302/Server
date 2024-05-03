package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;

@Getter
public class ImageToTextResponse {

  @Schema(type = "String", description = "이미지", example = "human_face.img")
  private String image;

  @Schema(type = "String", description = "성별", example = "female")
  private String gender;

  @Schema(type = "String", description = "감정", example = "기쁨")
  private String emotion;

  @Schema(type = "Map", description = "flask 서버에서 반환한 동물 키워드에 대한 json",
      example = "{\n"
          + "            \"deer\": 0.027949409559369087,\n"
          + "            \"cat\": 0.22635209560394287,\n"
          + "            \"dog\": 0.729890763759613\n"
          + "        }")
  private Map keyWord;

  public ImageToTextResponse(String image, String gender, String emotion, Map keyWord) {
    this.image = image;
    this.gender = gender;
    this.emotion = emotion;
    this.keyWord = keyWord;
  }

  public static ImageToTextResponse create(String image, String gender, String emotion,
      Map keyWord) {
    return new ImageToTextResponse(image, gender, emotion, keyWord);
  }
}
