package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "3D image api (논문용)", description = "3D 이미지 API by openAI")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image/openai")
public class OpenAIController {

  private final OpenAIService openAIService;

  @Operation(summary = "3D 이미지 반환", description = "이미지를 openAI API를 통해 3D 이미지 프롬포트를 반환한다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "키워드 반환"),
      @ApiResponse(responseCode = "400",
          description = "1. 파라미터가 부족합니다. \t\n"
              + "2. 올바르지 않은 파라미터 값입니다. \t\n"
              + "3. 올바르지 않은 JSON 형식입니다. \t\n"
              + "4. 지원하지 않는 형식의 데이터 요청입니다. \t\n"
              + "5. 멀티 파트가 부족합니다. \t\n"
              + "6. 파일의 용량이 너무 큽니다.",
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
              + "2. S3 업로드가 실패했습니다. \t\n"
              + "3. 이미지 파일만 업로드 가능합니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping(value = "/image_to_text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiStandardResponse<Map<String, String>>> imageToText(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(
          description = "multipart/form-data 형식의 10MB 이하 이미지 파일을 받습니다.",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      )
      @RequestPart("file") MultipartFile file) throws JsonProcessingException {
    Map<String, String> message = openAIService.imageToText(apiKey, file);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }
}
