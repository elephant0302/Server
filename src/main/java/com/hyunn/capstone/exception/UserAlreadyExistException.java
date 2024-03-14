package com.hyunn.capstone.exception;

public class UserAlreadyExistException extends RuntimeException {

  public UserAlreadyExistException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.USER_ALREADY_EXIST_EXCEPTION;
  }
}
