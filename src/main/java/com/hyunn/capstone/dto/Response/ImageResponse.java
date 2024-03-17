package com.hyunn.capstone.dto.Response;

import lombok.Getter;

// 유저에게 반환하는 dto
@Getter
public class ImageResponse {

  private String threeDimension;

  private String prompt;

  public ImageResponse(String threeDimension, String prompt) {
    this.threeDimension = threeDimension;
    this.prompt = prompt;
  }

  public static ImageResponse create(String threeDimension, String prompt) {
    return new ImageResponse(threeDimension, prompt);
  }
}
