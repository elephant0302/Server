package com.hyunn.capstone.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class KakaoPayReadyRequest {

  @Schema(type = "String", description = "핸드폰번호", example = "01012345678")
  @NotBlank(message = "핸드폰 번호를 입력하세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String partner_user_id;

  @Schema(type = "String", description = "상품명", example = "초코파이")
  @NotBlank(message = "상품명을 입력해주세요.")
  private String item_name;

  @Schema(type = "Integer", description = "상품 수량", example = "1")
  @NotNull(message = "상품 수량을 입력해주세요.")
  @Min(value = 1, message = "상품 수량은 최소 1 이상이어야 합니다.")
  private Integer quantity;

  @Schema(type = "Integer", description = "상품 총액", example = "1100")
  @NotNull(message = "상품 금액을 입력해주세요.")
  @Min(value = 1, message = "상품 금액은 최소 1 이상이어야 합니다.")
  private Integer total_amount;


  public KakaoPayReadyRequest(String partner_user_id, String item_name, Integer quantity,
      Integer total_amount) {
    this.partner_user_id = partner_user_id;
    this.item_name = item_name;
    this.quantity = quantity;
    this.total_amount = total_amount;

  }

  public static KakaoPayReadyRequest create(String partner_user_id, String item_name,
      Integer quantity, Integer total_amount) {
    return new KakaoPayReadyRequest(partner_user_id, item_name, quantity, total_amount);
  }

}