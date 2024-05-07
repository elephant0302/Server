package com.hyunn.capstone.exception;

public class PaymentNotFoundException extends RuntimeException {

  public PaymentNotFoundException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.PAYMENR_NOT_FOUND_EXCEPTION;
  }

}
