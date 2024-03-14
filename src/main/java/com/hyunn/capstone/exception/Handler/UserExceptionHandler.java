package com.hyunn.capstone.exception.Handler;

import static com.hyunn.capstone.exception.ErrorStatus.*;

import com.hyunn.capstone.controller.UserController;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.ErrorResponse;
import com.hyunn.capstone.exception.UserAlreadyExistException;
import com.hyunn.capstone.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class UserExceptionHandler {

  // 유저를 찾을 수 없는 경우
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiStandardResponse<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(e.toErrorCode(), e.getMessage());
    return ApiStandardResponse.fail(errorResponse);
  }

  // 휴면 계정이 있는 경우
  @ExceptionHandler(UserAlreadyExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleUserAlreadyExistException(
      UserAlreadyExistException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(e.toErrorCode(), e.getMessage());
    return ApiStandardResponse.fail(errorResponse);
  }

  // 파라미터가 올바르지 않은 경우 (validation에 걸린 경우)
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    log.error("", e);

    // ConstraintViolationException에서 발생한 오류 메세지 추출
    String errorMessage = "";
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      errorMessage = violation.getMessage();
      break; // 첫 번째 오류 메세지만 추출
    }

    final ErrorResponse errorResponse = ErrorResponse.create(INVALID_PARAMETER, errorMessage);
    return ApiStandardResponse.fail(errorResponse);
  }

  // 파라미터 값이 올바르지 않은 경우
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(INVALID_PARAMETER,
        "올바르지 않은 파라미터 값입니다.");
    return ApiStandardResponse.fail(errorResponse);
  }

  // 파라미터 값이 부족한 경우
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    log.error("", e);

    final ErrorResponse errorResponse = ErrorResponse.create(NEED_MORE_PARAMETER,
        "파라미터가 부족합니다.");
    return ApiStandardResponse.fail(errorResponse);
  }

  // Validation 오류
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.error("", e);

    BindingResult result = e.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();

    // error 메세지 추출
    String errors = fieldErrors.stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    final ErrorResponse errorResponse = ErrorResponse.create(VALIDATION_EXCEPTION, errors);
    return ApiStandardResponse.fail(errorResponse);
  }

  // validation 외 JSON 형식 오류
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiStandardResponse<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    ErrorResponse errorResponse = ErrorResponse.create(INVALID_JSON,
        "올바르지 않은 JSON 형식입니다.");
    return ApiStandardResponse.fail(errorResponse);
  }
}
