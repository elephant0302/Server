package com.hyunn.capstone.service;

import com.hyunn.capstone.dto.Request.UserRequest;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.ApiKeyNotValidException;
import com.hyunn.capstone.exception.ImageNotFoundException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  private final UserJpaRepository userJpaRepository;
  private final ImageJpaRepository imageJpaRepository;

  /**
   * 주소 업데이트 (카카오 정보에 주소가 없는 경우나 주소를 수정할 경우)
   */
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
  public String setImage(String apiKey, UserRequest userRequest, Long imageId) {
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
  public void deleteUser(String apiKey, UserRequest userRequest) {
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
    userJpaRepository.delete(existUser);
  }

  /**
   * 유저 정보로 관련 이미지 리스트로 반환하기
   */
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

}
