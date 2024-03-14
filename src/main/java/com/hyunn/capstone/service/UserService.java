package com.hyunn.capstone.service;

import com.hyunn.capstone.dto.Request.UserDeleteRequest;
import com.hyunn.capstone.dto.UserDto;
import com.hyunn.capstone.entity.User;
import com.hyunn.capstone.exception.UserAlreadyExistException;
import com.hyunn.capstone.exception.UserNotFoundException;
import com.hyunn.capstone.repository.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserJpaRepository userJpaRepository;

  public UserDto login(UserDto userDto) {
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
    return UserDto.create(newUser.getPhone(), newUser.getPassword(), newUser.getAddress());
  }

  public UserDto rollBack(Long userId) {
    Optional<User> user = Optional.ofNullable(userJpaRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();
    existUser.rollBackUser();
    userJpaRepository.save(existUser);
    return UserDto.create(existUser.getPhone(), existUser.getPassword(), existUser.getAddress());
  }

  public void deleteUser(UserDeleteRequest userDeleteRequest) {
    String phone = userDeleteRequest.getPhone();
    String password = userDeleteRequest.getPassword();

    // 계정이 없는 경우
    Optional<User> existUser = Optional.ofNullable(
        userJpaRepository.findUserByPhoneAndPassword(phone, password)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User deleteUser = existUser.get();
    deleteUser.deleteUser();
    userJpaRepository.save(deleteUser);
  }


}
