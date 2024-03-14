package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.Request.UserDeleteRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.UserDto;
import com.hyunn.capstone.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<ApiStandardResponse<UserDto>> login(
      @Valid @RequestBody UserDto requestUserDto) {
    UserDto userDto = userService.login(requestUserDto);
    return ResponseEntity.ok(ApiStandardResponse.success(userDto));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<ApiStandardResponse<UserDto>> rollBack(
      @PathVariable @Min(value = 1, message = "유저 ID는 1 이상의 정수입니다.") Long userId) {
    UserDto userDto = userService.rollBack(userId);
    return ResponseEntity.ok(ApiStandardResponse.success(userDto));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ApiStandardResponse<String>> deleteUser(
      @Valid @RequestBody UserDeleteRequest userDeleteRequest) {
    userService.deleteUser(userDeleteRequest);
    return ResponseEntity.ok(ApiStandardResponse.success("탈퇴 성공"));
  }

}
