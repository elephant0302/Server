package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PaymentResponse {

  @Schema(type = "Long", description = "결제 고유 아이디", example = "1")
  private Long paymentId;

  @Schema(type = "String", description = "상품명", example = "고양이 악세서리")
  private String productName;

  @Schema(type = "Integer", description = "가격", example = "10000")
  private Integer price;

  @Schema(type = "String", description = "배송지", example = "서울시 OO구 OO로 99")
  private String address;

  @Schema(type = "String", description = "배송 상태", example = "배송 대기")
  private String Shipping;

  @Schema(type = "String", description = "결제 고유 번호", example = "T658009429fd342ca505")
  private String tid;

  @Schema(type = "Long", description = "이미지 고유 아이디", example = "1")
  private Long imageId;

  @Schema(type = "String", description = "이미지", example = "image.jpg")
  private String image;

  @Schema(type = "String", description = "3D 생성물")
  private String threeDimension;

  @Schema(type = "String", description = "키워드", example = "고양이")
  private String keyWord;

  private PaymentResponse(Long paymentId, String productName, Integer price, String address,
      String Shipping, String tid, Long imageId, String image, String threeDimension,
      String keyWord) {
    this.paymentId = paymentId;
    this.productName = productName;
    this.price = price;
    this.address = address;
    this.Shipping = Shipping;
    this.tid = tid;
    this.imageId = imageId;
    this.image = image;
    this.threeDimension = threeDimension;
    this.keyWord = keyWord;
  }

  public static PaymentResponse create(Long paymentId, String productName, Integer price,
      String address, String Shipping, String tid, Long imageId, String image,
      String threeDimension, String keyWord) {
    return new PaymentResponse(paymentId, productName, price, address, Shipping, tid, imageId,
        image,
        threeDimension, keyWord);
  }

}
