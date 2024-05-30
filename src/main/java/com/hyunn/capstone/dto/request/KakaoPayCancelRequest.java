package com.hyunn.capstone.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class KakaoPayCancelRequest {

  @Schema(type = "String", description = "결제 고유 번호", example = "T1234567890123456789")
  @NotBlank(message = "결제 고유 번호를 입력하세요.")
  private String tid;

  public KakaoPayCancelRequest(String tid) {
    this.tid = tid;
  }

  public static KakaoPayCancelRequest create(String tid) {
    return new KakaoPayCancelRequest(tid);
  }

}
