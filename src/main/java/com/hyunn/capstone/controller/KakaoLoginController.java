package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.UserResponse;
import com.hyunn.capstone.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class KakaoLoginController {

  private final KakaoLoginService kakaoLoginService;

  @Operation(summary = "엑세스 토큰 발급 및 유저 생성 (자동 처리 로직)",
      description =
          "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=681c7dd24caab6868c553a07b27422ed&redirect_uri=http://localhost:9000/api/login/oauth2/code/kakaod에서 로그인 후"
              + "\n카카오측에서 바로 리다이렉션 되는 엔드포인트로써 자동으로 처리된다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "카카오 로그인 성공"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"01\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"INVALID_PARAMETER\", "
                  + "\"msg\":\"올바르지 않은 파라미터 값입니다.\"} }"))),
      @ApiResponse(responseCode = "404",
          description = "1. Api 응답이 올바르지 않습니다. \t\n"
              + "2. 유저를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @GetMapping("/oauth2/code/kakao")
  public ResponseEntity<ApiStandardResponse<UserResponse>> kakaoLogin(
      @Parameter(description = "카카오톡 코드", required = true, example = "code1234567890")
      @RequestParam("code") String code) {
    String accessToken = kakaoLoginService.getAccessToken(code);
    UserResponse userResponse = kakaoLoginService.getUserInfo(accessToken);
    return ResponseEntity.ok(ApiStandardResponse.success(userResponse));
  }

}
