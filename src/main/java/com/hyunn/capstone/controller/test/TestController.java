package com.hyunn.capstone.controller.test;

import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.Payment;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.PaymentJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "test api", description = "테스트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  private final UserJpaRepository userJpaRepository;
  private final ImageJpaRepository imageJpaRepository;
  private final PaymentJpaRepository paymentJpaRepository;

  @Operation(summary = "GET METHOD TEST", description = "GET 요청 테스트 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @GetMapping()
  public ResponseEntity<ApiStandardResponse<String>> getTestMessage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    return ResponseEntity.ok(ApiStandardResponse.success("(TEST) GET 요청을 받았습니다."));
  }

  @Operation(summary = "POST METHOD TEST", description = "POST 요청 테스트 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping()
  public ResponseEntity<ApiStandardResponse<String>> getPostMessage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    return ResponseEntity.ok(ApiStandardResponse.success("(TEST) POST 요청을 받았습니다."));
  }

  @Operation(summary = "PATCH METHOD TEST", description = "PATCH 요청 테스트 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PatchMapping()
  public ResponseEntity<ApiStandardResponse<String>> getPatchMessage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    return ResponseEntity.ok(ApiStandardResponse.success("(TEST) PATCH 요청을 받았습니다."));
  }

  @Operation(summary = "DELETE METHOD TEST", description = "DELETE 요청 테스트 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @DeleteMapping()
  public ResponseEntity<ApiStandardResponse<String>> getDeleteMessage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    return ResponseEntity.ok(ApiStandardResponse.success("(TEST) DELETE 요청을 받았습니다."));
  }

  @Operation(summary = "User DB 조회",
      description = "실제 유저 정보로써 테스트 시에는 유저 등록 후 자신의 정보로 테스트해주세요!"
          + "\nhttps://kauth.kakao.com/oauth/authorize?response_type=code&client_id=681c7dd24caab6868c553a07b27422ed&redirect_uri=https://capstone.hyunn.site/api/login/oauth2/code/kakao")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @GetMapping("/users")
  public ResponseEntity<ApiStandardResponse<List<User>>> getUserDB(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    List<User> userList = userJpaRepository.findAll();
    return ResponseEntity.ok(ApiStandardResponse.success(userList));
  }

  @Operation(summary = "Image DB 조회", description = "Image DB 확인 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @GetMapping("/images")
  public ResponseEntity<ApiStandardResponse<List<ImageDto>>> getImageDB(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    List<Image> imageList = imageJpaRepository.findAll();
    // Image 엔티티를 ImageDto로 변환
    List<ImageDto> imageDtoList = imageList.stream()
        .map(image -> ImageDto.create(
            image.getImageId(),
            image.getImage(),
            image.getThreeDimension(),
            image.getKeyWord(),
            image.getEmotion(),
            image.getGender(),
            image.getUser().getUserId() // 사용자 ID만 전달
        ))
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiStandardResponse.success(imageDtoList));
  }

  @Operation(summary = "Payment DB 조회", description = "Payment DB 확인 API 입니다.")
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @GetMapping("/payments")
  public ResponseEntity<ApiStandardResponse<List<PaymentDto>>> getPaymentDB(
      @RequestHeader(value = "x-api-key", required = false) String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }
    List<Payment> paymentList = paymentJpaRepository.findAll();
    // Payment 엔티티를 PaymentDto로 변환
    List<PaymentDto> paymentDtoList = paymentList.stream()
        .map(payment -> PaymentDto.create(
            payment.getPaymentId(),
            payment.getProductName(),
            payment.getPrice(),
            payment.getAddress(),
            payment.getShipping(),
            payment.getTid(),
            payment.getImage().getImageId()
        ))
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiStandardResponse.success(paymentDtoList));
  }

}
