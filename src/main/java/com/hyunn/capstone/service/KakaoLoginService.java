package com.hyunn.capstone.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyunn.capstone.dto.response.UserResponse;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiNotFoundException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
  private String authorizationGrantType;

  @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
  private String userInfoUri;

  private final UserJpaRepository userJpaRepository;

  /**
   * 엑세스 토큰 받아오기
   */
  public String getAccessToken(String code) {
    RestTemplate restTemplate = new RestTemplate();

    //HttpHeader 오브젝트
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //HttpBody 오브젝트
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", authorizationGrantType);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    ;
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("code", code);

    //http 바디(params)와 http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(tokenUri, HttpMethod.POST, kakaoTokenRequest, String.class);

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
    String accessToken = jsonObject.get("access_token").getAsString();

    return accessToken;
  }

  /**
   * 배송지 받아오기
   */
  public String getUserAddress(String accessToken) {
    String reqUri = "https://kapi.kakao.com/v1/user/shipping_address";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);

    //http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
        new HttpEntity<>(headers);

    //reqUrl로 Http 요청 , GET 방식
    ResponseEntity<String> response =
        restTemplate.exchange(reqUri, HttpMethod.GET, kakaoProfileRequest, String.class);

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

    // shipping_addresses 배열 가져오기
    JsonArray shippingAddresses = jsonObject.getAsJsonArray("shipping_addresses");

    // 주소가 존재하지 않는 경우 null 반환
    if (shippingAddresses == null || shippingAddresses.isJsonNull()) {
      return null;
    }

    // 첫 번째 주소의 base_address 가져오기
    JsonObject firstAddress = shippingAddresses.get(0).getAsJsonObject();
    String baseAddress = firstAddress.get("base_address").getAsString();

    return baseAddress;
  }

  /**
   * 로그인한 유저 정보 받아오기 -> 저장하기
   */
  @Transactional
  public UserResponse getUserInfo(String accessToken) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
        new HttpEntity<>(headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(userInfoUri, HttpMethod.POST, kakaoProfileRequest, String.class);

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
    String email = jsonObject.getAsJsonObject("kakao_account").get("email").getAsString();
    String nickname = jsonObject.getAsJsonObject("kakao_account").getAsJsonObject("profile")
        .get("nickname").getAsString();
    String phoneNum = jsonObject.getAsJsonObject("kakao_account").get("phone_number")
        .getAsString();

    // +82 10-1234-5678 -> 01012345678
    String phone = extractNumber(phoneNum);
    phone = phone.substring(2, 12);
    phone = "0" + phone;

    // 이미 회원가입을 한 유저라면 바로 처리
    if (userJpaRepository.existsUserByPhoneAndEmail(phone, email)) {
      Optional<User> user = Optional.ofNullable(
          userJpaRepository.findUserByPhoneAndEmail(phone, email)
              .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
      User existUser = user.get();
      existUser.updateAccessToken(accessToken);
      userJpaRepository.save(existUser);
      return UserResponse.create(existUser.getNickName(), existUser.getEmail(),
          existUser.getPhone(), existUser.getAddress(), existUser.getAccessToken());
    }

    // 배송지 받아오기
    String address = getUserAddress(accessToken);

    // 새로운 유저 저장
    User newUser = User.createUser(nickname, email, phone, address, accessToken);
    userJpaRepository.save(newUser);

    return UserResponse.create(nickname, email, phone, address, accessToken);
  }

  /**
   * 핸드폰 번호 파싱 함수
   */
  public static String extractNumber(String input) {
    StringBuilder sb = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (Character.isDigit(c)) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

}
