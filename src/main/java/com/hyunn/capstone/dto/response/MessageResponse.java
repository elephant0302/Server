package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MessageResponse {

  @Schema(type = "String", description = "이메일 발신에 대한 응답 값", example = "이메일 전송 성공! root@naver.com")
  private String responseByEmail;

  @Schema(type = "String", description = "문자 메세지 발신에 대한 응답 값", example = "문자 메시지 전송 성공! 010-1234-5678")
  private String responseBySMS;

  public MessageResponse(String responseByEmail, String responseBySMS) {
    this.responseByEmail = responseByEmail;
    this.responseBySMS = responseBySMS;
  }

  public static MessageResponse create(String responseByEmail, String responseBySMS) {
    return new MessageResponse(responseByEmail, responseBySMS);
  }
}
