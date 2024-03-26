package com.hyunn.capstone.exception;

public class RootUserException extends RuntimeException {

  public RootUserException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.ROOT_USER_EXCEPTION;
  }
}
