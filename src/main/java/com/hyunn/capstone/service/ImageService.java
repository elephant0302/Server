package com.hyunn.capstone.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hyunn.capstone.dto.Request.ImageToTextRequest;
import com.hyunn.capstone.dto.Response.ImageToTextResponse;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.S3UploadException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final AmazonS3Client amazonS3Client;

  /**
   * img_to_text (flask 서버에 이미지를 보내 키워드를 받아온다.)
   * 아직 flaks 서버가 제대로 구현되지 않아서 정확한 반환값을 받지 못함...
   */
  public ImageToTextResponse imageToText(String apiKey, MultipartFile multipartFile,
      ImageToTextRequest imageToTextRequest) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 이미지는 jpg로 변환 후 S3에 저장
    String image = uploadFile(multipartFile);

    String gender = imageToTextRequest.getGender();
    String emotion = imageToTextRequest.getEmotion();

    // API 엔드포인트 URL
    String apiUrl = "https://ai.hyunn.site/image";

    // HttpHeaders 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // MultiValueMap 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("image", image);

    // HttpEntity 설정
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // RestTemplate 생성
    RestTemplate restTemplate = new RestTemplate();

    // POST 요청 보내기
    ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity,
        String.class);

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

    List<String> keywords = Collections.singletonList(responseBody);

    return ImageToTextResponse.create(image, gender, emotion, keywords);
  }

  /**
   * S3로 파일 업로드
   */
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
