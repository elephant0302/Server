package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class KakaoPayApproveResponse {

  @Schema(type = "String", description = "요청 고유 번호 - 승인/취소가 구분된 결제번호", example = "A5678901234567890123")
  private String aid;

  @Schema(type = "String", description = "결제 고유 번호 - 승인/취소가 동일한 결제번호", example = "T1234567890123456789")
  private String tid;

  @Schema(type = "String", description = "가맹점 코드", example = "TC0ONETIME")
  private String cid;

  @Schema(type = "String", description = "가맹점 회원 id, 최대 100자", example = "partner_user_id")
  private String partner_user_id;

  @Schema(type = "String", description = "결제 수단, CARD 또는 MONEY 중 하나", example = "MONEY")
  private String payment_method_type;

  @Schema(type = "Amount", description = "결제 금액 정보", example = "{\n" +
      "    \"total\": 2200,\n" +
      "  },")
  private Amount amount;

  @Getter
  @Setter
  @ToString
  public static class Amount {

    private Integer total;        // 전체 결제 금액
  }

  @Schema(type = "String", description = "상품 이름, 최대 100자", example = "초코파이")
  private String item_name;

  @Schema(type = "String", description = "상품 코드, 최대 100자", example = "123421423")
  private String item_code;

  @Schema(type = "Integer", description = "상품 수량", example = "1")
  private int quantity;

  @Schema(type = "Datetime", description = "결제 준비 요청 시각", example = "2023-07-15T21:18:22")
  private LocalDateTime created_at;

  @Schema(type = "Datetime", description = "결제 승인 요청 시각", example = "2023-07-15T21:18:22")
  private LocalDateTime approved_at;


  public KakaoPayApproveResponse(String aid, String tid, String cid, String partner_user_id,
      String payment_method_type,
      Amount amount, String item_name, String item_code, int quantity,
      LocalDateTime created_at, LocalDateTime approved_at) {
    this.aid = aid;
    this.tid = tid;
    this.cid = cid;
    this.partner_user_id = partner_user_id;
    this.payment_method_type = payment_method_type;
    this.amount = amount;
    this.item_name = item_name;
    this.item_code = item_code;
    this.quantity = quantity;
    this.created_at = created_at;
    this.approved_at = approved_at;
  }

  public static KakaoPayApproveResponse create(String aid,
      String tid,
      String cid,
      String partner_user_id,
      String payment_method_type,
      Amount amount,
      String item_name,
      String item_code,
      Integer quantity,
      LocalDateTime created_at,
      LocalDateTime approved_at) {
    return new KakaoPayApproveResponse(aid, tid, cid, partner_user_id, payment_method_type, amount,
        item_name, item_code, quantity, created_at, approved_at);
  }
}
