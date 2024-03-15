package com.hyunn.capstone.exception;

public class ApiNotFoundException extends RuntimeException {

  public ApiNotFoundException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.API_NOT_FOUND_EXCEPTION;
  }

}
