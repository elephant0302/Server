package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.Request.UserRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/address")
  public ResponseEntity<ApiStandardResponse<String>> updateAddress(
      @Valid @RequestBody UserRequest userRequest,
      @RequestParam String address) {
    String message = userService.updateAddress(userRequest, address);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @PostMapping("/image")
  public ResponseEntity<ApiStandardResponse<String>> setImage(
      @Valid @RequestBody UserRequest userRequest,
      @RequestParam Long imageId) {
    String message = userService.setImage(userRequest, imageId);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @DeleteMapping()
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
