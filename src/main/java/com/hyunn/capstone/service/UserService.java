package com.hyunn.capstone.service;

import com.hyunn.capstone.dto.Request.UserRequest;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.dto.UserDto;
import com.hyunn.capstone.entity.Image;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.UserAlreadyExistException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.ImageJpaRepository;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserJpaRepository userJpaRepository;
  private final ImageJpaRepository imageJpaRepository;

  /**
   * 로그인
   */
  public UserDto login(UserDto userDto,Long imageId) {
    // userDto에서 필요 정보 추출
    String phone = userDto.getPhone();
    String password = userDto.getPassword();
    String address = userDto.getAddress();

    // 기존 계정이 있는 경우
    if (userJpaRepository.existsUserByPhoneAndPassword(phone, password)) {
      Optional<User> existUser = Optional.ofNullable(
          userJpaRepository.findUserByPhoneAndPassword(phone, password)
              .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

      // 휴면 계정이 존재하는 경우
      if (!existUser.get().getStatus()) {
        Long id = existUser.get().getUserId();
        throw new UserAlreadyExistException("휴면 계정이 존재합니다. id : " + id);
      }

      return UserDto.create(existUser.get().getPhone(), existUser.get().getPassword(),
          existUser.get().getAddress());
    }

    // 기존 계정이 없는 경우
    User newUser = User.createUser(phone, password, address);
    userJpaRepository.save(newUser);

    // image에 유저 연결
    Optional<Image> image = imageJpaRepository.findById(imageId);
    Image targetImage = image.get();
    targetImage.connectUser(newUser);
    imageJpaRepository.save(targetImage);

    return UserDto.create(newUser.getPhone(), newUser.getPassword(), newUser.getAddress());
  }

  /**
   * 휴면 계정 복구
   */
  public UserDto rollBack(Long userId) {
    Optional<User> user = Optional.ofNullable(userJpaRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();
    existUser.rollBackUser();
    userJpaRepository.save(existUser);
    return UserDto.create(existUser.getPhone(), existUser.getPassword(), existUser.getAddress());
  }

  /**
   * 계정 탈퇴 (휴면 계정으로 변경) -> 한달 후 삭제
   */
  public void deleteUser(UserRequest userRequest) {
    String phone = userRequest.getPhone();
    String password = userRequest.getPassword();

    // 계정이 없는 경우
    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndPassword(phone, password)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User deleteUser = existUser.get();
    deleteUser.deleteUser();
    userJpaRepository.save(deleteUser);
  }

  /**
   * 유저 정보로 관련 이미지 리스트로 반환하기
   */
  public List<ThreeDimensionResponse> findImagesByUser(UserRequest userRequest) {
    String phone = userRequest.getPhone();
    String password = userRequest.getPassword();

    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndPassword(phone, password)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    // 휴면 계정이 존재하는 경우, 없는 걸로 처리
    if (!existUser.get().getStatus()) {
      throw new UserAlreadyExistException("유저 정보를 가져오지 못했습니다.");
    }

    List<Image> images = imageJpaRepository.findAllByUser(existUser);

    // Entity에서 Dto로 변형
    List<ThreeDimensionResponse> imageResponses = images.stream()
        .map(image -> ThreeDimensionResponse.create(image.getImageId(), image.getImage(),
            image.getThreeDimension(), image.getKeyWord()))
        .collect(Collectors.toList());

    return imageResponses;
  }

  /**
   * 자동으로 삭제 대상 사용자를 삭제합니다.
   */
  public void deleteInactiveUsers() {
    // 1달 이상 활동이 없는 사용자를 조회합니다.
    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
    List<User> inactiveUsers = userJpaRepository.findInactiveUsers(oneMonthAgo);

    // 삭제 대상 사용자를 삭제합니다.
    for (User user : inactiveUsers) {
      userJpaRepository.delete(user);
    }
  }

}
