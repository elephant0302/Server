package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoPayApproveResponse {

  @Schema(description = "상품명")
  private String productName;

  @Schema(type = "Amount", description = "결제 가격", example = "{\n" +
      "    \"total\": 2200,\n" +
      "  },")
  private Amount amount;

  @Getter
  @Setter
  @ToString
  public static class Amount {

    private Integer total; // 전체 결제 금액
  }

  @Schema(description = "배송지 주소")
  private String address;

  @Schema(description = "배송 정보")
  private String shippingStatus;

  @Schema(type = "Long", description = "이미지 id", example = "1")
  private Long imageId;

  @Schema(description = "유저 닉네임")
  private String userNickname;

  @Schema(description = "유저 이메일")
  private String userEmail;

  @Schema(description = "승인 시간")
  private LocalDateTime approvedAt;

  @Schema(description = "휴대폰 번호")
  private String phoneNumber;

  @Schema(description = "3D 프린터 서버에 대한 응답값")
  private String message;

  public KakaoPayApproveResponse(String productName, Amount amount, String address,
      String shippingStatus, Long imageId, String userNickname, String userEmail,
      LocalDateTime approvedAt, String phoneNumber, String message) {
    this.productName = productName;
    this.amount = amount;
    this.address = address;
    this.shippingStatus = shippingStatus;
    this.imageId = imageId;
    this.userNickname = userNickname;
    this.userEmail = userEmail;
    this.approvedAt = approvedAt;
    this.phoneNumber = phoneNumber;
    this.message = message;
  }

  public static KakaoPayApproveResponse create(String productName, Amount amount, String address,
      String shippingStatus, Long imageId, String userNickname, String userEmail,
      LocalDateTime approvedAt, String phoneNumber, String message) {
    return new KakaoPayApproveResponse(productName, amount, address, shippingStatus, imageId,
        userNickname, userEmail, approvedAt, phoneNumber, message);
  }
}



