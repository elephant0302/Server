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

  private User(Long userId, String phone, String password) {
    this.userId = userId;
    this.phone = phone;
    this.password = password;
  }

  public static User createUser(Long userId, String phone, String password) {
    return new User(userId, phone, password);
  }

}