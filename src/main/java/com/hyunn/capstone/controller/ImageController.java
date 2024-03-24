package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Request.ImageRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.ThreeDimensionCreateResponse;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.service.MeshyApiService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "image api", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

  private final MeshyApiService meshyApiService;

  @Operation(summary = "키워드 반환", description = "이미지를 flask 서버로 보내 키워드를 반환받는다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "키워드 반환"),
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
              + "2. 이미지를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapieky1234")
  @PostMapping("/image_to_text")
  public void imageToText(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody ImageRequest imageRequest) {
    // flask 서버에 api가 존재해야함 -> 전달하면 키워드를 받는 식으로
  }

  @Operation(summary = "3D 모델 코드 생성", description = "3D 모델을 얻을 수 있는 코드를 받는다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "3D 모델 코드 반환"),
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
              + "2. 이미지를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapieky1234")
  @PostMapping("/text_to_3D/{keyWord}")
  public ResponseEntity<ApiStandardResponse<ThreeDimensionCreateResponse>> textTo3D(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "키워드", required = true, example = "고양이")
      @PathVariable String keyWord,
      @Valid @RequestBody ImageRequest imageRequest)
      throws JsonProcessingException {
    ThreeDimensionCreateResponse threeDimensionCreateResponse = meshyApiService.textTo3D(
        apiKey, imageRequest, keyWord);
    return ResponseEntity.ok(ApiStandardResponse.success(threeDimensionCreateResponse));
  }

  @Operation(summary = "3D obj 생성", description = "3D 모델 코드로 3D obj를 생성, 반환")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "3D obj 반환"),
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
              + "2. 이미지를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapieky1234")
  @GetMapping("/result/{previewResult}")
  public ResponseEntity<ApiStandardResponse<ThreeDimensionResponse>> return3D(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "3D 모델 코드", required = true, example = "")
      @PathVariable String previewResult) {
    ThreeDimensionResponse threeDimensionResponse = meshyApiService.return3D(apiKey, previewResult);
    return ResponseEntity.ok(ApiStandardResponse.success(threeDimensionResponse));
  }

  @Operation(summary = "3D obj 정제 (미사용 API)", description = "사용 여부가 결정되지 않음.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "3D obj 정제"),
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
              + "2. 이미지를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testapieky1234")
  @PostMapping("/refine/{previewResult}")
  public ResponseEntity<ApiStandardResponse<String>> refine3D(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "3D 모델 코드", required = true, example = "")
      @PathVariable String previewResult) throws JsonProcessingException {
    String message = meshyApiService.refine3D(apiKey, previewResult);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

}
