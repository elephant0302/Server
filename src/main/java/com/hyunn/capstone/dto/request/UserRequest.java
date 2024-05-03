package com.hyunn.capstone.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserRequest {

  @Schema(type = "String", description = "핸드폰 번호", example = "01012345678")
  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String phone;

  @Schema(type = "String", description = "카카오 이메일 (계정)", example = "root@naver.com")
  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식을 지켜주세요.")
  private String email;


  public UserRequest(String phone, String email) {
    this.phone = phone;
    this.email = email;
  }

  public static UserRequest create(String phone, String email) {
    return new UserRequest(phone, email);
  }
}
