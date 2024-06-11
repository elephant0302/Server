package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.request.KakaoPayReadyRequest;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.KakaoPayApproveResponse;
import com.hyunn.capstone.dto.response.KakaoPayCancelResponse;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Tag(name = "kakaopay api", description = "카카오페이 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class KakaoPayController {

  private final KakaoPayService kakaoPayService;
  @Value("${spring.security.oauth2.client.kakaoPay.client-id}")
  private String cid;

  @Operation(summary = "결제 요청", description = "결제 창을 제공되며 결제를 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "결제 창을 제공되며 결제를 진행합니다."),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다. \t\n"
              + "5. 해당 유저가 소유하고 있는 이미지가 아닙니다. \t\n"
              + "6. 해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.",
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
              + "3. 이미지를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapikey2024")
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

  @GetMapping("/success")
  public ModelAndView approvePayment(@RequestParam("pg_token") String pgToken)
      throws JsonProcessingException {
    KakaoPayApproveResponse kakaoPayApproveResponse = kakaoPayService.getApprove(pgToken);
    ModelAndView modelAndView = new ModelAndView("PaymentSuccess");
    modelAndView.addObject("response", kakaoPayApproveResponse); // 뷰로 데이터 전달
    return modelAndView;
  }

  /**
   * 결제 취소 요청
   */
  @Operation(summary = "결제 취소 요청", description = "이미 승인된 결제를 취소합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
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
              + "3. 이미지를 찾지 못했습니다. \t\n"
              + "4. 이미 환불된 결제입니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapikey2024")
  @PostMapping("/cancel")
  public ResponseEntity<ApiStandardResponse<KakaoPayCancelResponse>> cancelPayment(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @RequestParam("payment_id") Long paymentId
  ) throws JsonProcessingException {
    KakaoPayCancelResponse cancelResponse = kakaoPayService.cancelPayment(apiKey, paymentId);
    return ResponseEntity.ok(ApiStandardResponse.success(cancelResponse));
  }

  /**
   * 결제 도중 취소 처리
   */
  @GetMapping("/cancel")
  public String failPayment() {
    return "PaymentFail";
  }

  /**
   * 결제 도중 실패 처리
   */
  @GetMapping("/fail")
  public String errorPayment() {
    return "PaymentFail";
  }

}