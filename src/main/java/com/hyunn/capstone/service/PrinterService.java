package com.hyunn.capstone.service;

import com.hyunn.capstone.dto.Request.MessageRequest;
import com.hyunn.capstone.dto.Response.MessageResponse;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.FileNotAllowedException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PrinterService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${printerServer.apiUri}")
  private String PrinterApiUri;

  private final UserJpaRepository userJpaRepository;
  private final MessageService messageService;

  /**
   * 3D 프린터 서버에 obj 전송
   */
  public String sendObj(String apiKey, MultipartFile multipartFile, String phone) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 이미지 파일인지 확인
    if (!multipartFile.isEmpty() && !multipartFile.getOriginalFilename().toLowerCase()
        .endsWith(".obj")) {
      throw new FileNotAllowedException("obj 파일만 전송 가능합니다.");
    }

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    // Raspberry-Pi 요청 보내기
    RestTemplate restTemplate = new RestTemplate();

    // HttpHeaders 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("obj_url", multipartFile);
    requestBody.put("phone", phone);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        PrinterApiUri,
        HttpMethod.POST,
        requestEntity,
        String.class
    );

    // API 응답을 처리합니다.
    if (response.getStatusCode().is2xxSuccessful()) {
      // 성공적으로 API를 호출한 경우의 처리
      System.out.println("API 호출 성공: " + response.getBody());
    } else {
      // API 호출이 실패한 경우의 처리
      System.out.println("API 호출 실패: " + response.getStatusCode());
      throw new ApiNotFoundException("API 호출에 문제가 생겼습니다.");
    }

    // JSON 파싱
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    // responseBoby에 성공 코드에 따라서 추가 오류 처리 필요

    return new String(responseBody);
  }

  /**
   * 3D 프린터 서버에서 완료 요청
   */
  public MessageResponse commit(String apiKey, String phone) throws IOException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 유저 검색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();
    String email = existUser.getEmail();

    // 출력 완료에 대한 메시지 보내기
    MessageResponse messageResponse = messageService.sendMessage(apiKey,
        MessageRequest.create(phone, email), "출력이");

    return messageResponse;
  }

}
