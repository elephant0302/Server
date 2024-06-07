package com.hyunn.capstone.exception;

public class S3UploadException extends RuntimeException {

  public S3UploadException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.S3_UPLOAD_EXCEPTION;
  }
}