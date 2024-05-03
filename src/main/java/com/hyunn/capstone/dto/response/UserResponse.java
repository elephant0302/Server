package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserResponse {

  @Schema(type = "String", description = "카카오 닉네임", example = "홍길동")
  private final String nickName;

  @Schema(type = "String", description = "카카오 이메일 (계정)", example = "root@naver.com")
  private final String email;

  @Schema(type = "String", description = "핸드폰 번호", example = "01012345678")
  private final String phoneNum;

  @Schema(type = "String", description = "주소", example = "서울특별시 관악구 관악로 123")
  private final String address;

  @Schema(type = "String", description = "카카오에서 반환한 엑세스 토큰")
  private final String accessToken;

  public UserResponse(String nickName, String email, String phoneNum, String address,
      String accessToken) {
    this.nickName = nickName;
    this.email = email;
    this.phoneNum = phoneNum;
    this.address = address;
    this.accessToken = accessToken;
  }

  public static UserResponse create(String nickName, String email, String phoneNum, String address,
      String accessToken) {
    return new UserResponse(nickName, email, phoneNum, address, accessToken);
  }
}
