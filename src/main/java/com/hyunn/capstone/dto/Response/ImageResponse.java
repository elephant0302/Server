package com.hyunn.capstone.dto.Response;

import lombok.Getter;

// 유저에게 반환하는 dto
@Getter
public class ImageResponse {

  private String threeDimension;

  private String keyWord;

  public ImageResponse(String threeDimension, String keyWord) {
    this.threeDimension = threeDimension;
    this.keyWord = keyWord;
  }

  public static ImageResponse create(String threeDimension, String keyWord) {
    return new ImageResponse(threeDimension, keyWord);
  }
}
