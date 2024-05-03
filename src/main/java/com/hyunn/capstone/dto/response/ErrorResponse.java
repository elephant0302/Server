package com.hyunn.capstone.dto.response;

import com.hyunn.capstone.exception.ErrorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse {

  @Schema(type = "ErrorStatus", description = "응답 메세지")
  private final ErrorStatus status;

  @Schema(type = "String", description = "오류에 대한 간단한 메세지")
  private final String msg;

  public ErrorResponse(ErrorStatus status, String msg) {
    this.status = status;
    this.msg = msg;
  }

  public static ErrorResponse create(ErrorStatus code, String msg) {
    return new ErrorResponse(code, msg);
  }
}

