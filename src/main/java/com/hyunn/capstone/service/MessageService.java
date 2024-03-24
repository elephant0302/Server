package com.hyunn.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.capstone.dto.Request.MessageRequest;
import com.hyunn.capstone.dto.Response.MessageResponse;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MessageService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  @Value("${kakao-talk.redirect-uri}")
  private String kakaoTalkRedirectUri;

  @Value("${kakao-talk.message-uri}")
  private String kakaoTalkMessageUri;

  @Value("${spring.mail.username}")
  private String mailHost;

  @Value("${sendm.call-no}")
  private String senderNum;

  @Value("${sendm.message-uri}")
  private String sendmMessageUri;

  @Value("${sendm.master-id}")
  private String masterUserId;

  @Value("${sendm.api-key}")
  private String sendmApiKey;

  private final UserJpaRepository userJpaRepository;
  // 빨간 이유는 autowired 문제 (실행하는데 일단 문제는 없음!)
  private final JavaMailSender javaMailSender;

  /**
   * 이메일 발송
   */
  public String sendSimpleMail(String email, String nickName) {
    SimpleMailMessage message = new SimpleMailMessage();

    // 발신자
    message.setFrom(mailHost);

    // 수신자
    message.setTo(email);

    // 제목
    String title = "[JRGB] " + nickName + "님 주문하신 상품의 출력이 완료되었습니다.";
    message.setSubject(title);

    // 내용
    String text = "안녕하세요. JRGB입니다."
        + "\n" + nickName + "님이 주문하신 상품의 출력이 완료되었습니다. "
        + "\n더 많은 정보를 원하시면 아래 링크를 이용해주세요."
        + "\n" + kakaoTalkRedirectUri;
    message.setText(text);
    try {
      javaMailSender.send(message);
      return "이메일 전송 성공! " + email;
    } catch (MailException e) {
      return "이메일 전송 실패: " + e.getMessage();
    }
  }

  /**
   * 문자 메세지 발송
   */
  public String sendSMS(String phone, String nickName) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("user-id", masterUserId);
    headers.add("api-key", sendmApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("callerNo", senderNum);

    // 메세지
    String text = "[JRGB] " + nickName + "님 주문하신 상품의 출력이 완료되었습니다."
        + "\n홈페이지에 방문하셔서 확인해주세요.";
    ;
    requestBody.put("message", text);

    String receiveNum =
        phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7);
    requestBody.put("receiveNos", receiveNum);

    // HttpEntity를 생성합니다.
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // API 호출을 수행합니다.
    ResponseEntity<String> response = restTemplate.exchange(
        sendmMessageUri,
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
      throw new ApiNotFoundException("문자 메세지 API 호출에 문제가 생겼습니다.");
    }

    // JSON 파싱
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("문자 메세지 API 응답이 비어 있습니다.");
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode responseJson = mapper.readTree(responseBody);
    String code = responseJson.get("code").asText();
    String message = responseJson.get("message").asText();

    String result = "";
    if (code.equals("0")) {
      result = "문자 메시지 전송 성공! " + phone;
    } else {
      result = "문자 메시지 전송 실패: " + message;
    }
    return result;
  }

  /**
   * 각종 메세지 발송에 대한 응답 취합
   */
  public MessageResponse sendMessage(String apiKey, MessageRequest messageRequest)
      throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = messageRequest.getPhone();
    String email = messageRequest.getEmail();
    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    String nickName = existUser.get().getNickName();

    String responseByEmail = sendSimpleMail(email, nickName);
    String responseBySMS = sendSMS(phone, nickName);

    return MessageResponse.create(responseByEmail, responseBySMS);
  }

  /**
   * 카카오톡 발송
   */
  public String sendKakaoTalk(String apiKey, MessageRequest messageRequest)
      throws JsonProcessingException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = messageRequest.getPhone();
    String email = messageRequest.getEmail();

    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    String nickName = existUser.get().getNickName();
    String accessToken = existUser.get().getAccessToken();

    RestTemplate restTemplate = new RestTemplate();

    //HttpHeader 오브젝트
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //HttpBody 오브젝트
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    Map<String, Object> templateObject = new HashMap<>();
    templateObject.put("object_type", "text");

    // 메세지
    String text = "[JRGB] " + nickName + "님 주문하신 상품의 출력이 완료되었습니다.";
    templateObject.put("text", text);

    Map<String, String> link = new HashMap<>();
    link.put("web_url", kakaoTalkRedirectUri);
    link.put("mobile_web_url", kakaoTalkRedirectUri);
    templateObject.put("link", link);
    String templateJson = new ObjectMapper().writeValueAsString(templateObject);
    params.add("template_object", templateJson);

    //http 바디(params)와 http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(kakaoTalkMessageUri, HttpMethod.POST, kakaoTokenRequest,
            String.class);

    String result = "";
    if (response.getStatusCode() == HttpStatus.OK) {
      result = "카카오톡 전송 성공! " + phone;
    } else {
      result = "카카오톡 전송 실패: " + response.getBody();
    }
    return result;
  }

}
