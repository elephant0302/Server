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

  // 닉네임
  @Column(name = "nickname")
  private String nickName;

  // 이메일
  @Column(name = "email")
  private String email;

  // 전화번호
  @Column(name = "phone")
  private String phone;

  // 주소
  @Column(name = "address")
  private String address;


  private User(String nickName, String email, String phone, String address) {
    this.nickName = nickName;
    this.email = email;
    this.phone = phone;
    this.address = address;
  }

  public static User createUser(String nickName, String email, String phone, String address) {
    return new User(nickName, email, phone, address);
  }


  public void setAddress(String address) {
    this.address = address;
  }

}