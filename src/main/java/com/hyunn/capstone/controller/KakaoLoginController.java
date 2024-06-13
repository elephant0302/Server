package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.request.KakaoPayReadyRequest;
import com.hyunn.capstone.dto.request.UserRequest;
import com.hyunn.capstone.dto.response.KakaoPayReadyResponse;
import com.hyunn.capstone.dto.response.UserResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
import com.hyunn.capstone.service.KakaoLoginService;
import com.hyunn.capstone.service.KakaoPayService;
import com.hyunn.capstone.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class KakaoLoginController {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  private final KakaoLoginService kakaoLoginService;
  private final UserService userService;
  private final KakaoPayService kakaoPayService;
  private final ImageJpaRepository imageJpaRepository;
  private final UserJpaRepository userJpaRepository;

  @Hidden
  @GetMapping("/oauth2/code/kakao")
  public String kakaoLoginAndKakaoPay(
      @Parameter(description = "카카오톡 코드", required = true, example = "code1234567890")
      @RequestParam("code") String code, Model model) throws JsonProcessingException {
    // 카카오 로그인
    String accessToken = kakaoLoginService.getAccessToken(code);
    UserResponse userResponse = kakaoLoginService.getUserInfo(accessToken);

    // 가장 연결되지 않은 최근 이미지를 서칭
    Optional<User> user = userJpaRepository.findById(1L);
    Optional<Image> image = imageJpaRepository.findTopByUserOrderByDateDesc(user);
    Image topImage = image.get();

    // 그 이미지와 유저를 연결
    String setImage = userService.setImage(xApiKey, UserRequest.create(userResponse.getPhoneNum(), userResponse.getEmail()), topImage.getImageId());
    System.out.println(setImage);

    // 해당 이미지를 결제 (가격은 1000원)
    KakaoPayReadyResponse kakaoPayReadyResponse = kakaoPayService.getReady(topImage.getImageId(), xApiKey,
        KakaoPayReadyRequest.create(userResponse.getPhoneNum(), topImage.getKeyWord() + " accessory", 1000));

    // 카카오페이 URL을 활용한 결제 페이지로 리다이렉션
    String redirectUrl = kakaoPayReadyResponse.getNext_redirect_pc_url();
    model.addAttribute("redirectUrl", redirectUrl);
    return "KakaoLoginAndPay";
  }
}
