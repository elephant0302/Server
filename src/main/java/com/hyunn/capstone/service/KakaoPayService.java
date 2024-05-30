package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.request.KakaoPayCancelRequest;
import com.hyunn.capstone.dto.request.KakaoPayReadyRequest;
import com.hyunn.capstone.dto.response.KakaoPayApproveResponse;
import com.hyunn.capstone.dto.response.KakaoPayCancelResponse;
import com.hyunn.capstone.dto.response.KakaoPayReadyResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.Payment;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.UnauthorizedImageAccessException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.hyunn.capstone.exception.ImageNotFoundException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.PaymentJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
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
public class KakaoPayService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${spring.security.oauth2.client.kakaoPay.client-id}")
  private String cid;

  @Value("${spring.security.oauth2.client.kakaoPay.client-secret}")
  private String admin_Key;

  @Value("${spring.security.oauth2.client.kakaoPay.ready-uri}")
  private String readyUrl;

  @Value("${spring.security.oauth2.client.kakaoPay.approve-uri}")
  private String approveUrl;

  @Value("${spring.security.oauth2.client.kakaoPay.cancel-uri}")
  private String cancelUrl;

  @Value("${spring.security.oauth2.client.kakaoPay.redirect_approval_url}")
  private String redirect_approval_url;

  @Value("${spring.security.oauth2.client.kakaoPay.redirect_fail_url}")
  private String redirect_fail_url;

  @Value("${spring.security.oauth2.client.kakaoPay.redirect_cancel_url}")
  private String redirect_cancel_url;

  @Value("${spring.security.oauth2.client.kakaoPay.partner_order_id}")
  private String partner_order_id;

  private KakaoPayReadyResponse kakaoPayReadyResponse = KakaoPayReadyResponse.create();

  private final ImageJpaRepository imageJpaRepository;

  private final UserJpaRepository userJpaRepository;

  private final PaymentJpaRepository paymentJpaRepository;

  /**
   * 카카오페이 결제준비 단계
   */
  @Transactional
  public KakaoPayReadyResponse getReady(Long imageId, String apiKey,
      KakaoPayReadyRequest kakaoPayReadyRequest) throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    Optional<Image> image = Optional.ofNullable(imageJpaRepository.findById(imageId)
        .orElseThrow(() -> new ImageNotFoundException("이미지를 가져오지 못했습니다.")));

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(kakaoPayReadyRequest.getPartner_user_id())
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    if (image.get().getUser().getUserId() != user.get().getUserId()) {
      throw new UnauthorizedImageAccessException("해당 유저가 소유하고 있는 이미지가 아닙니다.");
    }
    // 새로 만들기

    String partner_user_id = kakaoPayReadyRequest.getPartner_user_id();
    // 요청 바디를 구성합니다.
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME");
    params.put("partner_order_id", partner_order_id);
    params.put("partner_user_id", partner_user_id);
    params.put("item_name", kakaoPayReadyRequest.getItem_name());
    params.put("quantity", 1);
    params.put("total_amount", kakaoPayReadyRequest.getTotal_amount());
    params.put("tax_free_amount", 0);
    params.put("approval_url", redirect_approval_url);
    params.put("cancel_url", redirect_cancel_url);
    params.put("fail_url", redirect_fail_url);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "SECRET_KEY " + admin_Key);
    headers.setContentType(MediaType.APPLICATION_JSON);
    String requestUrl = readyUrl;

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST,
        requestEntity, String.class);

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
    String tid = responseJson.get("tid").asText();
    String next_redirect_mobile_url = responseJson.get("next_redirect_mobile_url").asText();
    String next_redirect_pc_url = responseJson.get("next_redirect_pc_url").asText();

    kakaoPayReadyResponse.setNext_redirect_pc_url(next_redirect_pc_url);
    kakaoPayReadyResponse.setNext_redirect_mobile_url(next_redirect_mobile_url);
    kakaoPayReadyResponse.setTid(tid);
    kakaoPayReadyResponse.setPartner_user_id(partner_user_id);
    kakaoPayReadyResponse.setImageId(imageId);

    return kakaoPayReadyResponse;
  }

  /**
   * 카카오페이 결제승인 단계
   */
  @Transactional
  public KakaoPayApproveResponse getApprove(String pgToken) throws JsonProcessingException {

    // 요청 헤더를 구성합니다.
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "SECRET_KEY " + admin_Key);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 바디를 구성합니다.
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME");
    params.put("tid", kakaoPayReadyResponse.getTid());
    params.put("partner_order_id", partner_order_id);
    params.put("partner_user_id", kakaoPayReadyResponse.getPartner_user_id());
    params.put("pg_token", pgToken);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(approveUrl, HttpMethod.POST,
        requestEntity, String.class);

    // API 응답을 처리합니다.
    if (!response.getStatusCode().is2xxSuccessful()) {
      // API 호출이 실패한 경우의 처리
      throw new ApiNotFoundException("API 호출에 실패했습니다. 상태 코드: " + response.getStatusCode());
    }

    //JSON 파싱
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(responseBody);
    KakaoPayApproveResponse.Amount amount = new KakaoPayApproveResponse.Amount();
    amount.setTotal(responseJson.get("amount").get("total").asInt());

    // KakaoPayApproveResponse 생성을 위한 나머지 데이터 추출
    String item_name = responseJson.get("item_name").asText();

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(kakaoPayReadyResponse.getPartner_user_id())
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    Optional<Image> image = Optional.ofNullable(
        imageJpaRepository.findById(kakaoPayReadyResponse.getImageId())
            .orElseThrow(() -> new ImageNotFoundException("이미지 정보를 가져오지 못했습니다.")));

    Payment payment = Payment.createPayment(item_name, amount.getTotal().intValue(),
        user.get().getAddress(), "결제 완료", kakaoPayReadyResponse.getTid(),
        kakaoPayReadyResponse.getPartner_user_id(), image.get());
    paymentJpaRepository.save(payment);

    return KakaoPayApproveResponse.create(item_name, amount, payment.getAddress(), "결제 완료",
        image.get().getImageId(), user.get().getNickName(), user.get().getEmail(),
        payment.getDate(), user.get().getPhone());
  }

  /**
   * 카카오페이 결제 취소 단계
   */
  @Transactional
  public KakaoPayCancelResponse cancelPayment(String apiKey, String tid
  ) throws JsonProcessingException {
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // tid(결제 고유 번호)를 통해 결제 정보 조회
    Optional<Payment> p = Optional.ofNullable(
        paymentJpaRepository.findByTid(tid)
            .orElseThrow(() -> new ApiNotFoundException("tid를 통해 결제 정보를 찾을 수 없습니다.: " + tid)));

    Payment payment = p.get();
    // 유저 정보 조회
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(payment.getPartner_user_id())
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    // 취소 요청 파라미터 설정
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME");
    params.put("tid", tid);
    params.put("cancel_amount", payment.getPrice());
    params.put("cancel_tax_free_amount", 0);
    params.put("cancel_available_amount", payment.getPrice());

    // API 호출
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "SECRET_KEY " + admin_Key);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        cancelUrl, HttpMethod.POST, requestEntity, String.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new ApiNotFoundException("API 호출에 실패했습니다. 상태 코드: " + response.getStatusCode());
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(response.getBody());

    // 취소 금액 정보 파싱
    KakaoPayCancelResponse.CanceledAmount canceledAmount = new KakaoPayCancelResponse.CanceledAmount();
    canceledAmount.setTotal(responseJson.get("canceled_amount").get("total").asInt());
    canceledAmount.setTaxFree(responseJson.get("canceled_amount").get("tax_free").asInt());
    canceledAmount.setVat(responseJson.get("canceled_amount").get("vat").asInt());
// 마이너스로 가격 설정
    Payment newPayment = Payment.createPayment(payment.getProductName(), payment.getPrice()*-1,
        user.get().getAddress(), "결제 취소", tid, payment.getPartner_user_id(), payment.getImage());
    paymentJpaRepository.save(newPayment);
    // 성공 응답 반환
    return new KakaoPayCancelResponse(
        payment.getProductName(),
        canceledAmount,
        payment.getTid(),
        "결제 취소", // 상태는 취소로 설정
        user.get().getNickName(),
        user.get().getEmail(),
        LocalDateTime.now(), // 취소 시간
        user.get().getPhone()
    );
  }

}