package com.hyunn.capstone.exception.Handler;

import com.hyunn.capstone.dto.ApiStandardResponse;
import com.hyunn.capstone.dto.ErrorResponse;
import com.hyunn.capstone.exception.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ApiStandardResponse<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(
        ErrorStatus.METHOD_NOT_ALLOWED_EXCEPTION, "지원하지 않는 HTTP Method입니다.");
    return ApiStandardResponse.fail(errorResponse);
  }

  // 오류 해결을 위해서 잠시 주석 처리
//  @ExceptionHandler(DataAccessException.class)
//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  public ApiStandardResponse<ErrorResponse> handleDataAccessException(DataAccessException e) {
//    log.error("", e);
//
//    final ErrorResponse errorResponse = ErrorResponse.create(ErrorStatus.DATABASE_ERROR,
//        "데이터베이스에 오류가 발생했습니다.");
//    return ApiStandardResponse.fail(errorResponse);
//  }
}

