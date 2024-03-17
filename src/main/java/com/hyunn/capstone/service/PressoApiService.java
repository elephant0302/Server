package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.Request.ImageRequest;
import com.hyunn.capstone.dto.Response.ImageResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
public class PressoApiService {

  @Value("${3dpresso.apiKey}")
  private String pressoApiKey;

  @Value("${3dpresso.callBackUrl}")
  private String callBackUrl;

  @Value("${cloud.aws.credentials.accessKey}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secretKey}")
  private String secretKey;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final ImageJpaRepository imageJpaRepository;


  public ImageResponse textTo3D(ImageRequest imageRequest, String keyWord)
      throws JsonProcessingException, InterruptedException {
    String gender = imageRequest.getGender();
    String emotion = imageRequest.getEmotion();

    String apiUrl = "https://api.3dpresso.ai/prod/api/v2/task";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + pressoApiKey);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    String name = createName(keyWord);
    requestBody.put("fileName", name);
    requestBody.put("engineName", "text_to_3d");
    requestBody.put("description", gender + " " + emotion + " " + keyWord);

    // params 섹션을 구성합니다.
    Map<String, Object> params = new HashMap<>();
    String prompt = gender + ", " + emotion + ", " + keyWord;
    params.put("prompt", prompt);
    requestBody.put("params", params);
    requestBody.put("callbackUrl", callBackUrl);

    // destinationContext 섹션을 구성합니다.
    Map<String, Object> destinationContext = new HashMap<>();
    destinationContext.put("type", "s3");
    destinationContext.put("accessKeyId", accessKey);
    destinationContext.put("secretAccessKey", secretKey);
    destinationContext.put("bucketName", bucketName);
    destinationContext.put("folder", "/JRGB");
    requestBody.put("destinationContext", destinationContext);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
        HttpMethod.PUT,
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
    String seq = responseJson.get("seq").asText();

    Thread.sleep(2 * 60 * 1000);
    String threeDimension = return3D(seq);

    Image newImage = Image.createImage(imageRequest.getImage(), threeDimension, keyWord, emotion, gender);
    imageJpaRepository.save(newImage);
    return new ImageResponse(threeDimension ,prompt);

  }

  public String createName(String keyWord) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    ZoneId koreaZoneId = ZoneId.of("Asia/Seoul"); // 대한민국 시간대
    String currentDateTime = ZonedDateTime.now(koreaZoneId).format(formatter);
    return currentDateTime + "_" + keyWord;
  }

  public String return3D(String seq) throws JsonProcessingException {
    String apiUrl = "https://api.3dpresso.ai/prod/api/v2/task";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + pressoApiKey);

    String seqUrl = "?seq=" + seq;
    apiUrl += seqUrl;

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
    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(responseBody);
    String url = responseJson.get("url").asText();

    return url;
  }
}
