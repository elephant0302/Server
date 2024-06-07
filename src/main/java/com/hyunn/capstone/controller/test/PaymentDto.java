package com.hyunn.capstone.controller.test;

import lombok.Getter;

@Getter
public class PaymentDto {

  private Long paymentId;

  private String productName;

  private Integer price;

  private String address;

  private String Shipping;

  private String tid;

  private Long imageId;

  public PaymentDto(Long paymentId, String productName, Integer price, String address,
      String Shipping,
      String tid, Long imageId) {
    this.paymentId = paymentId;
    this.productName = productName;
    this.price = price;
    this.address = address;
    this.Shipping = Shipping;
    this.tid = tid;
    this.imageId = imageId;
  }

  public static PaymentDto create(Long paymentId, String productName, Integer price, String address,
      String Shipping,
      String tid, Long imageId) {
    return new PaymentDto(paymentId, productName, price, address, Shipping, tid, imageId);
  }

}