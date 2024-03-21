package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.Request.KakaoTalkRequest;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoTalkService {

  @Value("${kakao-talk.redirect-uri}")
  private String kakaoTalkRedirectUri;

  private final UserJpaRepository userJpaRepository;

  /**
   * 카카오톡 발송
   */
  public String sendMessage(KakaoTalkRequest kakaoTalkRequest) throws JsonProcessingException {
    String phone = kakaoTalkRequest.getPhone();
    String email = kakaoTalkRequest.getEmail();
    String message = kakaoTalkRequest.getMessage();

    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    String accessToken = existUser.get().getAccessToken();

    String reqUri = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    RestTemplate restTemplate = new RestTemplate();

    //HttpHeader 오브젝트
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //HttpBody 오브젝트
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    Map<String, Object> templateObject = new HashMap<>();
    templateObject.put("object_type", "text");
    templateObject.put("text", message);
    Map<String, String> link = new HashMap<>();
    link.put("web_url", "https://developers.kakao.com");
    link.put("mobile_web_url", "https://developers.kakao.com");
    templateObject.put("link", link);
    String templateJson = new ObjectMapper().writeValueAsString(templateObject);
    params.add("template_object", templateJson);

    //http 바디(params)와 http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(reqUri, HttpMethod.POST, kakaoTokenRequest, String.class);

    String result = "";
    if (response.getStatusCode() == HttpStatus.OK) {
      result = "메시지 전송 성공!" + phone;
    } else {
      result = "메시지 전송 실패: " + response.getBody();
    }
    return result;
  }

}
