package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.UserResponse;
import com.hyunn.capstone.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class KakaoLoginController {

  private final KakaoLoginService kakaoLoginService;

  @GetMapping("/oauth2/code/kakao")
  public ResponseEntity<ApiStandardResponse<UserResponse>> kakaoLogin(
      @Parameter(description = "카카오톡 코드", required = true, example = "code1234567890")
      @RequestParam("code") String code) {
    String accessToken = kakaoLoginService.getAccessToken(code);
    UserResponse userResponse = kakaoLoginService.getUserInfo(accessToken);
    return ResponseEntity.ok(ApiStandardResponse.success(userResponse));
  }

}
