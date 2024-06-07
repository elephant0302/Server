package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiStandardResponse<T> {

  @Schema(type = "String", description = "응답 코드")
  private final String code;

  @Schema(type = "String", description = "응답 메세지")
  private final String msg;

  @Schema(type = "T", description = "응답 값")
  private final T data;

  private ApiStandardResponse(String code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static <T> ApiStandardResponse<T> success(T data) {
    return new ApiStandardResponse<>("00", "success", data);
  }

  public static <T extends ErrorResponse> ApiStandardResponse<T> fail(T data) {
    return new ApiStandardResponse<>(data.getStatus().toString(), "fail", data);
  }
}