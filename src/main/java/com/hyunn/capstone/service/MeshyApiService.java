package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyunn.capstone.dto.request.ThreeDimensionRequest;
import com.hyunn.capstone.dto.response.MeshyAPIResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MeshyApiService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${meshy.api-key}")
  private String meshyApiKey;

  private final ImageJpaRepository imageJpaRepository;
  private final UserJpaRepository userJpaRepository;

  /**
   * text_to_3d
   */
  @Transactional
  public MeshyAPIResponse textTo3D(String apiKey, String keyWord,
      ThreeDimensionRequest threeDimensionRequest)
      throws JsonProcessingException, InterruptedException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    Optional<User> rootUser = Optional.ofNullable(userJpaRepository.findById(1L)
        .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    String image = threeDimensionRequest.getImage();
    String gender = threeDimensionRequest.getGender();
    String emotion = threeDimensionRequest.getEmotion();

    String apiUri = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + meshyApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mode", "preview");
    String prompt = "3D model of " + "(" + gender + ") " + keyWord + ", " + emotion + ", "
        + "detailed, cartoon style, only face";
    requestBody.put("prompt", prompt);

    String artStyle = "cartoon";
    requestBody.put("art_style", artStyle);

    String negativePrompt = "ugly, low quality";
    requestBody.put("negative_prompt", negativePrompt);

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
    JsonNode responseJson = mapper.readTree(responseBody);
    String previewResult = responseJson.get("result").asText();

    // 3D 모델까지 시간이 걸리므로 올바른 값을 받아올 때까지 반복
    JsonObject modelUrls = null;
    while (true) {
      modelUrls = return3D(previewResult);
      if (modelUrls != null) {
        break;
      }
      Thread.sleep(10000); // 10초 대기
    }

    // 루트 유저에게 일단 할당
    String obj = modelUrls.get("obj").getAsString();
    Image newImage = Image.createImage(image, obj, keyWord, emotion, gender, rootUser.get());
    imageJpaRepository.save(newImage);

    // JsonObject를 Map<String, String> 변환
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> modelUrlsMap = objectMapper.readValue(modelUrls.toString(),
        new TypeReference<Map<String, String>>() {
        });

    return MeshyAPIResponse.create(newImage.getImageId(), newImage.getImage(), modelUrlsMap,
        newImage.getKeyWord());
  }

  /**
   * 3D 모델 반환
   */
  @Transactional
  public JsonObject return3D(String preview_result) {

    String apiUri = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + meshyApiKey);

    String resultUrl = "/" + preview_result;
    apiUri += resultUrl;

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUri,
        HttpMethod.GET,
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

    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
    String status = jsonObject.get("status").getAsString();

    if (status.equals("SUCCEEDED")) {
      return jsonObject.getAsJsonObject("model_urls");
    }
    return null;
  }

  /**
   * 3D 모델 정제
   */
  public String refine3D(String apiKey, String preview_result) throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String apiUri = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + meshyApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mode", "refine");
    requestBody.put("preview_task_id", preview_result);
    requestBody.put("texture_richness", "medium");

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
    JsonNode responseJson = mapper.readTree(responseBody);
    String result = responseJson.get("result").asText();

    return result;
  }
}