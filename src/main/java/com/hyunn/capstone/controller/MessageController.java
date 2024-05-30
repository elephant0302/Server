package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.request.MessageRequest;
import com.hyunn.capstone.dto.response.ApiStandardResponse;
import com.hyunn.capstone.dto.response.MessageResponse;
import com.hyunn.capstone.service.MessageService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "message api", description = "메세지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

  private final MessageService messageService;

  /**
   * 이메일 및 문자 보내기 (출력 완료시)
   */
  @Hidden
  @PostMapping()
  public ResponseEntity<ApiStandardResponse<MessageResponse>> sendMessage(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody MessageRequest messageRequest) throws IOException {
    MessageResponse messageResponse = messageService.sendMessage(apiKey, messageRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(messageResponse));
  }

  /**
   * 카카오톡 보내기 (사업자 등록 문제로 보류)
   */
  @Hidden
  @Parameter(name = "x-api-key", description = "x-api-key", schema = @Schema(type = "string"),
      in = ParameterIn.HEADER, example = "testApiKey2024")
  @PostMapping("/kakao")
  public ResponseEntity<ApiStandardResponse<String>> sendKakaoTalk(
      @RequestHeader(value = "x-api-key", required = false) String apiKey,
      @Valid @RequestBody MessageRequest messageRequest) throws JsonProcessingException {
    String result = messageService.sendKakaoTalk(apiKey, messageRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(result));
  }
}
