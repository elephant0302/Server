package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyunn.capstone.dto.Request.ImageRequest;
import com.hyunn.capstone.dto.Response.ImageResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
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
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MeshyApiService {

  @Value("${meshy.apiKey}")
  private String meshyApiKey;

  private final ImageJpaRepository imageJpaRepository;

  /**
   * 키워드를 3D로 변경 후 이미지 DB에 저장
   */
  public ImageResponse textTo3D(ImageRequest imageRequest, String keyWord)
      throws JsonProcessingException, InterruptedException {
    String gender = imageRequest.getGender();
    String emotion = imageRequest.getEmotion();

    String apiUrl = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + meshyApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mode", "preview");
    requestBody.put("prompt", keyWord);

    String art_style = "realistic";
    requestBody.put("art_style", art_style);

    // 프롬포트는 추가적인 수정이 필요함.
    String prompt = gender + ", " + emotion;
    requestBody.put("negative_prompt", prompt);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
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
    String preview_result = responseJson.get("result").asText();

    String result = refine3D(preview_result);
    String threeDimension = return3D(result);

    Image newImage = Image.createImage(imageRequest.getImage(), threeDimension, keyWord, emotion,
        gender);
    imageJpaRepository.save(newImage);
    return new ImageResponse(threeDimension, prompt);

  }

  /**
   * 3D refine 작업
   */
  public String refine3D(String preview_result) throws JsonProcessingException {
    String apiUrl = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + meshyApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("mode", "refine");
    requestBody.put("preview_task_id", preview_result);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
        HttpMethod.POST,
        requestEntity,
        String.class
    );

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

  /**
   * 3D 모델 반환
   */
  public String return3D(String result) throws JsonProcessingException {
    String apiUrl = "https://api.meshy.ai/v2/text-to-3d";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + meshyApiKey);

    String resultUrl = "/" + result;
    apiUrl += resultUrl;

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
        HttpMethod.GET,
        requestEntity,
        String.class
    );

    // JSON 파싱
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

    String status = jsonObject.get("status").getAsString();
    if (status != "SUCCEEDED") {
      throw new ApiNotFoundException("3D 생성 실패 : " + status);
    }
    String threeDimensionUrl = jsonObject.getAsJsonObject("model_urls").get("obj").getAsString();

    return threeDimensionUrl;
  }

}

