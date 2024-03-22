package com.hyunn.capstone.dto.Response;

import lombok.Getter;

@Getter
public class MessageResponse {

  private String responseByEmail;

  private String responseBySMS;

  public MessageResponse(String responseByEmail, String responseBySMS) {
    this.responseByEmail = responseByEmail;
    this.responseBySMS = responseBySMS;
  }

  public static MessageResponse create(String responseByEmail, String responseBySMS) {
    return new MessageResponse(responseByEmail, responseBySMS);
  }
}
