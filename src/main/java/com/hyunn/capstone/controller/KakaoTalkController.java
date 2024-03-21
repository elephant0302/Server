package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Request.KakaoTalkRequest;
import com.hyunn.capstone.dto.Response.ApiStandardResponse;
import com.hyunn.capstone.service.KakaoTalkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/talk")
public class KakaoTalkController {

  private final KakaoTalkService kakaoTalkService;

  @PostMapping()
  public ResponseEntity<ApiStandardResponse<String>> sendKakaoTalk(
      @Valid @RequestBody KakaoTalkRequest kakaoTalkRequest) throws JsonProcessingException {
    String message = kakaoTalkService.sendMessage(kakaoTalkRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

}
