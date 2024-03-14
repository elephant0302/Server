package com.hyunn.capstone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserDto {
  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String phone;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
      message = "비밀번호는 영문자, 숫자, 특수문자를 모두 포함하여 8자리 이상이어야 합니다.")
  private String password;

  @NotBlank(message = "도로명 주소를 입력해주세요.")
  private String address;

  public UserDto(String phone, String password, String address) {
    this.phone = phone;
    this.password = password;
    this.address = address;
  }

  public static UserDto create(String phone, String password, String address) {
    return new UserDto(phone, password, address);
  }
}
