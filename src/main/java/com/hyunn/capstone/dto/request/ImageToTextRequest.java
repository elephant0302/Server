package com.hyunn.capstone.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ImageToTextRequest {

  @Schema(type = "String", description = "성별", example = "female")
  @NotBlank(message = "성별을 입력해주세요.")
  private String gender;

  @Schema(type = "String", description = "감정", example = "happy")
  @NotBlank(message = "감정을 입력해주세요.")
  private String emotion;

  public ImageToTextRequest(String gender, String emotion) {
    this.gender = gender;
    this.emotion = emotion;
  }

  public static ImageToTextRequest create(String gender, String emotion) {
    return new ImageToTextRequest(gender, emotion);
  }
}
