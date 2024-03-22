package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Request.MessageRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.dto.Response.MessageResponse;
import com.hyunn.capstone.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

  private final MessageService messageService;

  @PostMapping()
  public ResponseEntity<ApiStandardResponse<MessageResponse>> sendMessage(
      @Valid @RequestBody MessageRequest messageRequest) throws JsonProcessingException {
    MessageResponse messageResponse = messageService.sendMessage(messageRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(messageResponse));
  }

  @PostMapping("/kakao")
  public ResponseEntity<ApiStandardResponse<String>> sendKakaoTalk(
      @Valid @RequestBody MessageRequest messageRequest) throws JsonProcessingException {
    String result = messageService.sendKakaoTalk(messageRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(result));
  }
}
