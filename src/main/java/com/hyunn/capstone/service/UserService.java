package com.hyunn.capstone.service;

import com.hyunn.capstone.dto.request.UserRequest;
import com.hyunn.capstone.dto.response.PaymentResponse;
import com.hyunn.capstone.dto.response.ThreeDimensionResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.Payment;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ImageNotFoundException;
import com.hyunn.capstone.exception.RootUserException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.PaymentJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  private final UserJpaRepository userJpaRepository;
  private final ImageJpaRepository imageJpaRepository;
  private final PaymentJpaRepository paymentJpaRepository;

  /**
   * 주소 업데이트 (카카오 정보에 주소가 없는 경우나 주소를 수정할 경우)
   */
  @Transactional
  public String updateAddress(String apiKey, UserRequest userRequest, String address) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = userRequest.getPhone();
    String email = userRequest.getEmail();

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    existUser.updateAddress(address);
    userJpaRepository.save(existUser);
    return "이메일이 성공적으로 등록되었습니다." + address;
  }

  /**
   * 이미지 등록
   */
  @Transactional
  public String setImage(String apiKey, UserRequest userRequest, Long imageId) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = userRequest.getPhone();
    String email = userRequest.getEmail();

    if (phone.equals("01012345678") && email.equals("root@naver.com")) {
      throw new RootUserException("해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.");
    }

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    Optional<Image> image = Optional.ofNullable(
        imageJpaRepository.findById(imageId)
            .orElseThrow(() -> new ImageNotFoundException("이미지 정보를 가져오지 못했습니다.")));
    Image existImage = image.get();
    existImage.connectUser(existUser);
    imageJpaRepository.save(existImage);

    String message = "";
    if (existUser.getUserId() == existImage.getUser().getUserId()) {
      message = "성공적으로 연결되었습니다.";
    } else {
      message = "연결에 실패했습니다. 다시 시도해주세요.";
    }

    return message;
  }

  /**
   * 유저 삭제
   */
  @Transactional
  public void deleteUser(String apiKey, UserRequest userRequest) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = userRequest.getPhone();
    String email = userRequest.getEmail();

    if (phone.equals("01012345678") && email.equals("root@naver.com")) {
      throw new RootUserException("해당 계정은 로직을 위한 루트 계정으로 해당 서비스를 지원하지 않습니다.");
    }

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    userJpaRepository.delete(existUser);
  }

  /**
   * 유저 정보로 관련 이미지 리스트로 반환하기
   */
  @Transactional
  public List<ThreeDimensionResponse> findImagesByUser(String apiKey, UserRequest userRequest) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = userRequest.getPhone();
    String email = userRequest.getEmail();

    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    List<Image> images = imageJpaRepository.findAllByUser(existUser);

    // Entity에서 Dto로 변형
    List<ThreeDimensionResponse> imageResponses = images.stream()
        .map(image -> ThreeDimensionResponse.create(image.getImageId(), image.getImage(),
            image.getThreeDimension(), image.getKeyWord()))
        .collect(Collectors.toList());

    return imageResponses;
  }

  /**
   * 유저 정보로 관련 결제 정보 리스트로 반환하기
   */
  @Transactional
  public List<PaymentResponse> findPaymentByUser(String apiKey, UserRequest userRequest) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    String phone = userRequest.getPhone();
    String email = userRequest.getEmail();

    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndEmail(phone, email)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    List<Payment> payments = paymentJpaRepository.findAllByUser(existUser);

    // Entity에서 Dto로 변형
    List<PaymentResponse> paymentResponses = payments.stream()
        .map(payment -> PaymentResponse.create(payment.getPaymentId(),
            payment.getProductName(), payment.getPrice(), payment.getAddress(),
            payment.getShipping(),
            payment.getTid(), payment.getImage().getImageId(), payment.getImage().getImage(),
            payment.getImage().getThreeDimension(), payment.getImage().getKeyWord()))
        .collect(Collectors.toList());

    return paymentResponses;
  }

}
