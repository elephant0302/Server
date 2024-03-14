package com.hyunn.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@Getter
@ToString(exclude = "userId")
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  // 전화번호
  @Column(name = "phone")
  private String phone;

  // 비밀번호
  @Column(name = "password")
  private String password;

  // 주소
  @Column(name = "address")
  private String address;

  // 회원 상태
  @Column(name = "status")
  private Boolean status;

  private User(String phone, String password, String address) {
    this.phone = phone;
    this.password = password;
    this.address = address;
    this.status = true;
  }

  public static User createUser(String phone, String password, String address) {
    return new User(phone, password, address);
  }

  public void deleteUser() {
    this.status = false;
  }

  public void rollBackUser() {
    this.status = true;
  }

}