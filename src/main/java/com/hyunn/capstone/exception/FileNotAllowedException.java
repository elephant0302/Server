package com.hyunn.capstone.exception;

public class FileNotAllowedException extends RuntimeException {

  public FileNotAllowedException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.FILE_NOT_ALLOWED_EXCEPTION;
  }

}