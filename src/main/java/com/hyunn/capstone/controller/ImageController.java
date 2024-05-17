package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.request.ThreeDimensionCreateRequest;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.ImageToTextResponse;
import com.hyunn.capstone.dto.response.ThreeDimensionCreateResponse;
import com.hyunn.capstone.dto.response.ThreeDimensionResponse;
import com.hyunn.capstone.service.ImageService;
import com.hyunn.capstone.service.MeshyApiService;
import io.swagger.v3.oas.annotations.Hidden;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "image api", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

  private final MeshyApiService meshyApiService;
  private final ImageService imageService;

  @Operation(summary = "키워드 반환", description = "이미지를 flask 서버로 보내 키워드를 반환받는다.")
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
      @ApiResponse(responseCode = "404",
          description = "1. Api 응답이 올바르지 않습니다. \t\n"
              + "2. S3 업로드가 실패했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping(value = "/image_to_text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiStandardResponse<ImageToTextResponse>> imageToText(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "성별", required = true, example = "female")
      @RequestParam("gender") String gender,
      @Parameter(description = "감정", required = true, example = "happy")
      @RequestParam("emotion") String emotion,
      @Parameter(
          description = "multipart/form-data 형식의 10MB 이하 이미지 파일을 받습니다.",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      )
      @RequestPart("file") MultipartFile file) throws JsonProcessingException {
    ImageToTextResponse imageToTextResponse = imageService.imageToText(apiKey, file,
        gender, emotion);
    return ResponseEntity.ok(ApiStandardResponse.success(imageToTextResponse));
  }

  @Operation(summary = "3D 모델 코드 생성 (유료)", description = "3D 모델을 얻을 수 있는 코드를 받는다. 60회 가능")
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
              + "2. 이미지를 찾지 못했습니다. \t\n"
              + "3. S3 업로드가 실패했습니다.",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "{ \"code\": \"10\", \"msg\": \"fail\","
                  + " \"data\": {\"status\": \"API_NOT_FOUND_EXCEPTION\", "
                  + "\"msg\":\"Api 응답이 올바르지 않습니다.\"} }")))})
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/text_to_3D/{keyWord}")
  public ResponseEntity<ApiStandardResponse<ThreeDimensionCreateResponse>> textTo3D(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "3개의 키워드 중 가장 닮은 키워드", required = true, example = "dog")
      @PathVariable String keyWord,
      @Valid @RequestBody ThreeDimensionCreateRequest threeDimensionCreateRequest)
      throws JsonProcessingException {
    ThreeDimensionCreateResponse threeDimensionCreateResponse = meshyApiService.textTo3D(
        apiKey, keyWord, threeDimensionCreateRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(threeDimensionCreateResponse));
  }

  @Operation(summary = "3D obj 반환", description = "3D 모델 코드를 사용하여 3D obj를 생성 후 저장한다."
      + "\nthreeDimension의 경우 실행 중일 때는 퍼센트를 반환하고 완료되면 3D 코드를 반환한다.")
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
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @GetMapping("/result/{previewResult}")
  public ResponseEntity<ApiStandardResponse<ThreeDimensionResponse>> return3D(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Parameter(description = "3D 모델 코드", required = true)
      @PathVariable String previewResult) {
    ThreeDimensionResponse threeDimensionResponse = meshyApiService.return3D(apiKey, previewResult);
    return ResponseEntity.ok(ApiStandardResponse.success(threeDimensionResponse));
  }

  /**
   * 3D 모델 정제 (사용 보류)
   */
  @Hidden
  @PostMapping("/refine/{previewResult}")
  public ResponseEntity<ApiStandardResponse<String>> refine3D(
      @RequestHeader String apiKey, @PathVariable String previewResult)
      throws JsonProcessingException {
    String message = meshyApiService.refine3D(apiKey, previewResult);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

}
