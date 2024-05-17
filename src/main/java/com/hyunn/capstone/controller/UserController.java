package com.hyunn.capstone.controller;

import com.hyunn.capstone.dto.request.UserRequest;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.ThreeDimensionResponse;
import com.hyunn.capstone.service.UserService;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user api", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  @Operation(summary = "주소 업데이트", description = "해당 유저의 주소를 업데이트한다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "주소 업데이트 성공"),
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
              + "2. 유저를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/address")
  public ResponseEntity<ApiStandardResponse<String>> updateAddress(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody UserRequest userRequest,
      @Parameter(description = "주소", required = true, example = "서울특별시 관악구 관악로 123")
      @RequestParam("address") String address) {
    String message = userService.updateAddress(apiKey, userRequest, address);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @Operation(summary = "이미지 등록", description = "이미지를 출력하기 위해 로그인을 한 유저에게 이미지를 할당한다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "이미지 등록 성공"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다. \t\n"
              + "5. 해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.",
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
              + "2. 이미지를 찾지 못했습니다. \t\n"
              + "3. 유저를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/image")
  public ResponseEntity<ApiStandardResponse<String>> setImage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody UserRequest userRequest,
      @Parameter(description = "이미지 ID", required = true, example = "1 (Long)")
      @RequestParam("imageId") Long imageId) {
    String message = userService.setImage(apiKey, userRequest, imageId);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @Operation(summary = "유저 삭제", description = "해당 유저 정보를 삭제한다..")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "유저 삭제 성공"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다.\t\n"
              + "5. 해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.",
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
              + "2. 유저를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @DeleteMapping()
  public ResponseEntity<ApiStandardResponse<String>> deleteUser(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody UserRequest userRequest) {
    userService.deleteUser(apiKey, userRequest);
    return ResponseEntity.ok(ApiStandardResponse.success("탈퇴 성공"));
  }

  @Operation(summary = "이미지 내역 조회", description = "해당 유저의 이미지 내역을 조회한다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "이미지 내역 성공"),
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
              + "2. 유저를 찾지 못했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/images")
  public ResponseEntity<ApiStandardResponse<List<ThreeDimensionResponse>>> findImagesByUser(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody UserRequest userRequest) {
    List<ThreeDimensionResponse> images = userService.findImagesByUser(apiKey, userRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(images));
  }

}
