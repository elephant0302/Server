package com.hyunn.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "payment")
@Getter
@ToString(exclude = "paymentId")
@NoArgsConstructor
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long paymentId;

  // 상품명
  @Column(name = "product_name")
  private String productName;

  // 상품 가격
  @Column(name = "price")
  private Integer price;

  // 배송지 -> 유저 주소랑 다를 수 있음
  @Column(name = "address")
  private String address;

  // 배송 정보
  @Column(name = "Shipping")
  private String Shipping;

  // 결제 고유 번호
  @Column(name = "tid")
  private String tid;

  // 전화번호
  @Column(name = "partner_user_id")
  private String partner_user_id;

  // 이미지와 1대1
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Image image;

  private Payment(String productName, Integer price, String address, String Shipping, String tid,
      String partner_user_id, Image image) {
    this.productName = productName;
    this.price = price;
    this.address = address;
    this.Shipping = Shipping;
    this.tid = tid;
    this.partner_user_id = partner_user_id;
    this.image = image;
  }

  public static Payment createPayment(String productName, Integer price, String address,
      String Shipping, String tid, String partner_user_id, Image image) {
    return new Payment(productName, price, address, Shipping, tid, partner_user_id, image);
  }

  public void updateShipping(String Shipping) {
    this.Shipping = Shipping;
  }

}
