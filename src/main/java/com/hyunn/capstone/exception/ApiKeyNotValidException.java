package com.hyunn.capstone.exception;


public class ApiKeyNotValidException extends RuntimeException {

  public ApiKeyNotValidException(final String message) {
    super(message);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.AUTHENTICATION_EXCEPTION;
  }
}