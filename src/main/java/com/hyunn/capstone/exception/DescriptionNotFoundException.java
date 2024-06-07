package com.hyunn.capstone.exception;

public class DescriptionNotFoundException extends RuntimeException {

  public DescriptionNotFoundException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.DESCRIPTION_NOT_FOUND_EXCEPTION;
  }

}