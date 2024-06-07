package com.hyunn.capstone.exception;

public class ImageNotFoundException extends RuntimeException {

  public ImageNotFoundException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.IMAGE_NOT_FOUND_EXCEPTION;
  }

}