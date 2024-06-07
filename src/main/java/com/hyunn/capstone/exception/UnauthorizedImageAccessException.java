package com.hyunn.capstone.exception;

public class UnauthorizedImageAccessException extends RuntimeException {

  public UnauthorizedImageAccessException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.UNAUTHORIZED_IMAGE_ACCESS_EXCEPTION;
  }
}