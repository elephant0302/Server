package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class KakaoLoginResponse {

  private final String nickName;

  private final String email;

  private final String phoneNum;

  private final String address;

  public KakaoLoginResponse(String nickName, String email, String phoneNum, String address) {
    this.nickName = nickName;
    this.email = email;
    this.phoneNum = phoneNum;
    this.address = address;
  }

  public static KakaoLoginResponse create(String nickName, String email, String phoneNum, String address) {
    return new KakaoLoginResponse(nickName, email, phoneNum, address);
  }
}
