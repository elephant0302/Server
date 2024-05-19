package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.request.KakaoPayReadyRequest;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.KakaoPayApproveResponse;
import com.hyunn.capstone.dto.response.KakaoPayReadyResponse;
import com.hyunn.capstone.service.KakaoPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "kakaopay api", description = "카카오페이 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class KakaoPayController {

  private final KakaoPayService kakaoPayService;
  @Value("${spring.security.oauth2.client.kakaoPay.client-id}")
  private String cid;

  @Operation(summary = "결제 준비 요청", description = "결제 준비 단계, 사용자는 이 단계를 거쳐 결제 승인을 받습니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "결제 준비 성공, 사용자는 결제 승인을 진행할 수 있습니다."),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다. \t\n"
              + "5. 해당 유저가 소유하고 있는 이미지가 아닙니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"01\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"INVALID_PARAMETER\", "
                  + "\"msg\":\"올바르지 않은 파라미터 값입니다.\"} }"))),
      @ApiResponse(responseCode = "403",
          description = "API KEY가 올바르지 않습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"12\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"AUTHENTICATION_EXCEPTION\", "
                  + "\"msg\":\"API KEY가 올바르지 않습니다.\"} }"))),
      @ApiResponse(responseCode = "404",
          description = "1. Api 응답이 올바르지 않습니다. \t\n"
              + "2. 유저를 찾지 못했습니다. \t\n"
              + "3. 이미지를 찾지 못했습니다. \t\n",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapikey1234")
  @PostMapping("/ready")
  public ResponseEntity<ApiStandardResponse<KakaoPayReadyResponse>> getReady(
      @Parameter(description = "이미지 ID", required = true, example = "1 (Long)")
      @RequestParam("image_id") Long imageId,
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody KakaoPayReadyRequest kakaoPayReadyRequest)
      throws JsonProcessingException {
    KakaoPayReadyResponse kakaoPayReadyResponse = kakaoPayService.getReady(imageId, apiKey,
        kakaoPayReadyRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(kakaoPayReadyResponse));
  }


  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapikey1234")
  @GetMapping("/success")
  public ResponseEntity<ApiStandardResponse<KakaoPayApproveResponse>> approvePayment(
      @RequestParam("pg_token") String pgToken) throws JsonProcessingException {
    KakaoPayApproveResponse kakaoPayApproveResponse = kakaoPayService.getApprove(pgToken);
    return ResponseEntity.ok(ApiStandardResponse.success(kakaoPayApproveResponse));
  }


}

