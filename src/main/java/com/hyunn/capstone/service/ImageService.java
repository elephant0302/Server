package com.hyunn.capstone.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.response.ImageToTextResponse;
import com.hyunn.capstone.entity.Description;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.DescriptionNotFoundException;
import com.hyunn.capstone.exception.FileNotAllowedException;
import com.hyunn.capstone.exception.S3UploadException;
import com.hyunn.capstone.repository.DescriptionJpaRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
public class ImageService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${flask.apiUri}")
  private String apiUri;

  private final AmazonS3Client amazonS3Client;

  private final DescriptionJpaRepository descriptionJpaRepository;

  /**
   * img_to_text (flask 서버에 이미지를 보내 키워드를 받아온다.)
   */
  @Transactional
  public ImageToTextResponse imageToText(String apiKey, MultipartFile multipartFile,
      String gender, String emotion) throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 이미지 파일인지 확인
    if (!multipartFile.isEmpty() && !multipartFile.getContentType().startsWith("image")) {
      throw new FileNotAllowedException("이미지 파일만 업로드 가능합니다.");
    }

    // 이미지는 jpg로 변환 후 S3에 저장
    String image = uploadFile(multipartFile);

    // flask 서버로 요청 보내기
    RestTemplate restTemplate = new RestTemplate();

    // HttpHeaders 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("image_url", image);

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

    // JSON 문자열을 파싱하여 JsonNode 객체로 변환
    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(responseBody);

    List<String> keys = new ArrayList<>();
    List<Double> values = new ArrayList<>();

    // 각 항목의 키와 값을 출력
    responseJson.fields().forEachRemaining(entry -> {
      keys.add(entry.getKey());
      values.add(entry.getValue().asDouble());
    });

    // 다시 객체로 만들어서 반환한다. + 가장 높게 나온 동물을 저장한다.
    Double maxValue = (double) 0;
    String maxKey = "";
    Map<String, Double> keyWordMap = new HashMap<>();
    for (int i = 0; i < keys.size(); i++) {
      keyWordMap.put(keys.get(i), values.get(i));
      if (values.get(i) > maxValue) {
        maxValue = values.get(i);
        maxKey = keys.get(i);
      }
    }

    // 성별과 동물로 관련 정보를 가져온다.
    Optional<Description> explain = Optional.ofNullable(
        descriptionJpaRepository.findByKeywordAndGender(maxKey, gender)
            .orElseThrow(() -> new DescriptionNotFoundException("설명 정보를 가져오지 못했습니다.")));

    return ImageToTextResponse.create(image, gender, emotion, keyWordMap, explain.get().getTitle(),
        explain.get().getExample());
  }

  /**
   * S3로 파일 업로드
   */
  @Transactional
  public String uploadFile(MultipartFile multipartFile) {

    if (multipartFile.getSize() == 0) {
      throw new S3UploadException("파일이 존재하지 않습니다.");
    }

    String fileType = multipartFile.getContentType();
    String originalFileName = multipartFile.getOriginalFilename();
    String uploadFileName = getUuidFileName(originalFileName);
    String uploadFileUrl = "";

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(multipartFile.getSize());
    objectMetadata.setContentType(multipartFile.getContentType());

    try (InputStream inputStream = multipartFile.getInputStream()) {

      // ex) 구분/파일.확장자
      String keyName = fileType + "/" + uploadFileName;

      // S3에 폴더 및 파일 업로드
      amazonS3Client.putObject(
          new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata));

      // S3에 업로드한 폴더 및 파일 URL
      uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

    } catch (IOException e) {
      throw new S3UploadException(fileType + "의 S3 업로드가 실패했습니다.");
    }

    return uploadFileUrl;
  }

  /**
   * UUID 파일명 반환
   */
  public String getUuidFileName(String fileName) {
    String ext = fileName.substring(fileName.indexOf(".") + 1);
    return UUID.randomUUID().toString() + "." + ext;
  }
}
