package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyunn.capstone.dto.request.ThreeDimensionCreateRequest;
import com.hyunn.capstone.dto.response.ThreeDimensionCreateResponse;
import com.hyunn.capstone.dto.response.ThreeDimensionResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.ImageNotFoundException;
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
  public ThreeDimensionCreateResponse textTo3D(String apiKey, String keyWord,
      ThreeDimensionCreateRequest threeDimensionCreateRequest)
      throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    Optional<User> rootUser = Optional.ofNullable(userJpaRepository.findById(1L)
        .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    String image = threeDimensionCreateRequest.getImage();
    String gender = threeDimensionCreateRequest.getGender();
    String emotion = threeDimensionCreateRequest.getEmotion();

    String apiUri = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + meshyApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mode", "preview");
    requestBody.put("prompt", keyWord);

    String artStyle = "realistic";
    requestBody.put("art_style", artStyle);

    // 프롬포트는 추가적인 수정이 필요함.
    String negativePrompt = gender + ", " + emotion + ", toy";
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

    // 루트 유저에게 일단 할당
    Image newImage = Image.createImage(image, previewResult, keyWord, emotion, gender,
        rootUser.get());
    imageJpaRepository.save(newImage);
    return ThreeDimensionCreateResponse.create(newImage.getImageId(), newImage.getThreeDimension(),
        newImage.getKeyWord());
  }

  /**
   * 3D 모델 반환
   */
  public ThreeDimensionResponse return3D(String apiKey, String preview_result) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

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
    String progress = jsonObject.get("progress").getAsString();
    String keyWord = jsonObject.get("prompt").getAsString(); // 키워드

    // 응답값 설정
    String message = null; // 퍼센트 or 3D 이미지
    Long userId = 1L;
    String image = null;

    if (status.equals("IN_PROGRESS")) {
      message = "progress : " + progress + "%";
    } else if (status.equals("SUCCEEDED")) {
      message = jsonObject.getAsJsonObject("model_urls").get("obj")
          .getAsString();

      // image 임시키로 쓰이던 preview_result를 3D url로 수정
      Optional<Image> existImage = Optional.ofNullable(
          imageJpaRepository.findImageByThreeDimension(preview_result)
              .orElseThrow(() -> new ImageNotFoundException("이미지 정보를 가져오지 못했습니다.")));

      Image targetImage = existImage.get();
      targetImage.update3D(message);
      imageJpaRepository.save(targetImage);

      userId = targetImage.getImageId();
      image = targetImage.getImage();
      message = targetImage.getThreeDimension();
    }

    return ThreeDimensionResponse.create(userId, image, message, keyWord);
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

