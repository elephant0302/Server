package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class KakaoPayCancelResponse {

  @Schema(description = "상품명")
  private String productName;

  @Schema(type = "Amount", description = "취소된 금액", example = "{\n" +
      "    \"total\": 2200,\n" +
      "    \"tax_free\": 0,\n" +
      "    \"vat\": 200,\n" +
      "  },")
  private CanceledAmount canceledAmount;

  @Getter
  @Setter
  @ToString
  public static class CanceledAmount {

    private Integer total;        // 취소된 전체 금액
    private Integer taxFree;      // 취소된 비과세 금액
    private Integer vat;          // 취소된 부가세 금액
  }

  @Schema(description = "결제 고유 번호")
  private String tid;

  @Schema(description = "결제 상태")
  private String status;

  @Schema(description = "유저 닉네임")
  private String userNickname;

  @Schema(description = "유저 이메일")
  private String userEmail;

  @Schema(description = "취소 시간")
  private LocalDateTime canceledAt;

  @Schema(description = "휴대폰 번호")
  private String phoneNumber;

  public KakaoPayCancelResponse(String productName, CanceledAmount canceledAmount, String tid, String status, String userNickname, String userEmail, LocalDateTime canceledAt, String phoneNumber) {
    this.productName = productName;
    this.canceledAmount = canceledAmount;
    this.tid = tid;
    this.status = status;
    this.userNickname = userNickname;
    this.userEmail = userEmail;
    this.canceledAt = canceledAt;
    this.phoneNumber = phoneNumber;
  }

  public static KakaoPayCancelResponse create(String productName, CanceledAmount canceledAmount, String tid, String status, String userNickname, String userEmail, LocalDateTime canceledAt, String phoneNumber) {
    return new KakaoPayCancelResponse(productName, canceledAmount, tid, status, userNickname, userEmail, canceledAt, phoneNumber);
  }
}
