package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class UserResponse {

  private final String nickName;

  private final String email;

  private final String phoneNum;

  private final String address;

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
