package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.MessageResponse;
import com.hyunn.capstone.service.PrinterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "printer api", description = "3D 프린터 통신 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/printer")
public class PrinterController {

  private final PrinterService printerService;

  @Operation(summary = "프린터 서버에 obj 전송", description = "obj 파일을 3D printer 서버로 전송한다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "전송 성공"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"01\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"INVALID_PARAMETER\", "
                  + "\"msg\":\"올바르지 않은 파라미터 값입니다.\"} }"))),
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
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping()
  public ResponseEntity<ApiStandardResponse<String>> sendObj(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "이미지 id", required = true, example = "1 (Long)")
      @RequestParam("image_id") Long imageId) throws JsonProcessingException {
    String response = printerService.sendObj(apiKey, imageId);
    return ResponseEntity.ok(ApiStandardResponse.success(response));
  }

  @Operation(summary = "완성 메세지 받기", description = "3D printer 서버에서 완료 요청.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "완료 반영 성공"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.",
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
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/commit")
  public ResponseEntity<ApiStandardResponse<MessageResponse>> commit(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "핸드폰 번호", required = true, example = "01012345678")
      @RequestParam("phone") String phone) throws IOException {
    MessageResponse response = printerService.commit(apiKey, phone);
    return ResponseEntity.ok(ApiStandardResponse.success(response));
  }

}
