package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.Request.UserRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.dto.UserDto;
import com.hyunn.capstone.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping("/login/{imageId}")
  public ResponseEntity<ApiStandardResponse<UserDto>> login(
      @PathVariable @Min(value = 1, message = "이미지 ID는 1 이상의 정수입니다.") Long imageId,
      @Valid @RequestBody UserDto requestUserDto) {
    UserDto userDto = userService.login(requestUserDto, imageId);
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
      @Valid @RequestBody UserRequest userRequest) {
    userService.deleteUser(userRequest);
    return ResponseEntity.ok(ApiStandardResponse.success("탈퇴 성공"));
  }

  @GetMapping("/images")
  public ResponseEntity<ApiStandardResponse<List<ThreeDimensionResponse>>> findImagesByUser(
      @Valid @RequestBody UserRequest userRequest) {
    List<ThreeDimensionResponse> images = userService.findImagesByUser(userRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(images));
  }

}
