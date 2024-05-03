package com.hyunn.capstone.exception.handler;

import static com.hyunn.capstone.exception.ErrorStatus.FILE_SIZE_EXCEED_EXCEPTION;

import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.ErrorResponse;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // x-api-key 헤더가 올바르지 않은 경우
  @ExceptionHandler(ApiKeyNotValidException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiStandardResponse<ErrorResponse> handleApiKeyNotValidException(
      ApiKeyNotValidException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(e.toErrorCode(), e.getMessage());
    return ApiStandardResponse.fail(errorResponse);
  }

  // 지원하지 않는 HTTP Method를 사용했을 경우
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ApiStandardResponse<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(
        ErrorStatus.METHOD_NOT_ALLOWED_EXCEPTION, "지원하지 않는 HTTP Method입니다.");
    return ApiStandardResponse.fail(errorResponse);
  }

  // tomcat에서 바로 http 오류를 보내기 때문에 Global로 처리!
  // 업로드 용량이 부족한 경우
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(FILE_SIZE_EXCEED_EXCEPTION,
        "10MB를 넘는 파일은 업로드가 불가능합니다.");
    return ApiStandardResponse.fail(errorResponse);
  }

  // 오류 처리를 위해서 잠시 주석 처리했음
//  // 데이터 베이스 오류
//  @ExceptionHandler(DataAccessException.class)
//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  public ApiStandardResponse<ErrorResponse> handleDataAccessException(DataAccessException e) {
//    log.error("", e);
//
//    final ErrorResponse errorResponse = ErrorResponse.create(ErrorStatus.DATABASE_ERROR,
//        "데이터베이스에 오류가 발생했습니다.");
//    return ApiStandardResponse.fail(errorResponse);
//  }
//
//  // 내부 서버 오류
//  @ExceptionHandler(InternalServerError.class)
//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  public ApiStandardResponse<ErrorResponse> handleInternalServerError(InternalServerError e) {
//    log.error("", e);
//
//    final ErrorResponse errorResponse = ErrorResponse.create(ErrorStatus.INTERNAL_SERVER_ERROR,
//        "예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.");
//    return ApiStandardResponse.fail(errorResponse);
//  }
}

