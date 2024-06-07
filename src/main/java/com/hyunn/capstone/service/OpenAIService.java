package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.FileNotAllowedException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OpenAIService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${openai.api-key}")
  private String openAIKey;

  private final ImageService imageService;

  /**
   * img_to_text (chat gpt 4o)
   */
  @Transactional
  public Map<String, String> imageToText(String apiKey, MultipartFile multipartFile)
      throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 이미지 파일인지 확인
    if (!multipartFile.isEmpty() && !multipartFile.getContentType().startsWith("image")) {
      throw new FileNotAllowedException("이미지 파일만 업로드 가능합니다.");
    }

    // 이미지는 jpg로 변환 후 S3에 저장
    String imageUrl = imageService.uploadFile(multipartFile);

    String apiUri = "https://api.openai.com/v1/chat/completions";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + openAIKey);

    // 요청 메시지 구성
    Map<String, Object> messageContentText = new HashMap<>();
    messageContentText.put("type", "text");
    messageContentText.put("text",
        "당신은 제시된 이미지를 text-3d 모델링으로 변환해주는 생성형 AI의 프롬프트를 만드는 프롬프트 전문가입니다. 입력한 이미지를 3D 모델링으로 변환할 수 있도록 구체적으로 묘사해 주세요. 그림 전체를 묘사할 필요 없이, 그림에서 가장 핵심적인 부분에 대해 자세히 묘사하면 됩니다. 영문으로 작성해 주시고, 그림의 핵심적인 부분을 구체적으로 묘사해주세요.");

    Map<String, Object> messageContentImage = new HashMap<>();
    messageContentImage.put("type", "image_url");
    messageContentImage.put("image_url", Map.of("url", imageUrl));

    Map<String, Object> message = new HashMap<>();
    message.put("role", "user");
    message.put("content", new Object[]{messageContentText, messageContentImage});

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-4-turbo");
    requestBody.put("messages", new Object[]{message});

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUri,
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

    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(response.getBody());
    String content = responseJson.get("choices").get(0).get("message").get("content").asText();

    // 원하는 데이터 형식으로 변환
    Map<String, String> resultData = new HashMap<>();
    resultData.put("message", content);
    resultData.put("image", imageUrl);

    return resultData;
  }
}