package com.hyunn.capstone.exception;


public class AlreadyRefundedException extends RuntimeException {

  public AlreadyRefundedException(final String message) {
    super(message);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.ALREADY_REFUNDED_EXCEPTION;
  }
}