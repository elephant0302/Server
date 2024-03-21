package com.hyunn.capstone.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class KakaoTalkRequest {

  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String phone;

  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;

  @NotBlank(message = "카카오톡 메세지를 입력해주세요. (200자 제한)")
  private final String message;

  public KakaoTalkRequest(String phone, String email, String message) {
    this.phone = phone;
    this.email = email;
    this.message = message;
  }

  public static KakaoTalkRequest create(String phone, String email, String message) {
    return new KakaoTalkRequest(phone, email, message);
  }

}
