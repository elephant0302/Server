package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.request.MessageRequest;
import com.hyunn.capstone.dto.response.MessageResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.Payment;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.PaymentNotFoundException;
import com.hyunn.capstone.exception.RootUserException;
import com.hyunn.capstone.repository.PaymentJpaRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PrinterService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${printerServer.apiUri}")
  private String PrinterApiUri;

  private final PaymentJpaRepository paymentJpaRepository;
  private final MessageService messageService;

  /**
   * 3D 프린터 서버에 obj 전송
   */
  @Transactional
  public String sendObj(Image image) throws JsonProcessingException {
    String obj = image.getThreeDimension();
    Long userId = image.getUser().getUserId();
    Long paymentId = image.getPayment().getPaymentId();

    // 결제 정보 검사
    if (!paymentJpaRepository.existsById(paymentId)) {
      throw new PaymentNotFoundException("결제 정보를 가져오지 못했습니다.");
    }

    // 루트 사용자 제한
    if (userId == 1) {
      throw new RootUserException("해당 유저는 루트 유저로써 해당 기능을 수행할 수 없습니다.");
    }

    // Raspberry-Pi 요청 보내기
    RestTemplate restTemplate = new RestTemplate();

    // HttpHeaders 설정
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-api-key", xApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("obj_url", obj);
    requestBody.put("payment_id", paymentId);

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

    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(responseBody);
    String message = responseJson.get("message").asText();

    if (!message.equals("success")) {
      throw new ApiNotFoundException("3D 프린터 서버에서 예상치 못한 오류가 발생했습니다.");
    }

    return new String(message);
  }

  /**
   * 3D 프린터 서버에서 완료 요청
   */
  @Transactional
  public MessageResponse commit(String apiKey, Long paymentId) throws IOException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 결제 정보 검사
    Optional<Payment> payment = Optional.ofNullable(
        paymentJpaRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 가져오지 못했습니다.")));

    User existUser = payment.get().getImage().getUser();
    String phone = existUser.getPhone();
    String email = existUser.getEmail();

    // 루트 사용자 제한
    if (existUser.getUserId() == 1) {
      throw new RootUserException("해당 유저는 루트 유저로써 해당 기능을 수행할 수 없습니다.");
    }

    // DB에 반영
    Payment existPayment = payment.get();
    existPayment.updateShipping("배송 시작");
    paymentJpaRepository.save(existPayment);

    // 출력 완료에 대한 메시지 보내기
    MessageResponse messageResponse = messageService.sendMessage(apiKey,
        MessageRequest.create(phone, email));

    return messageResponse;
  }

}
