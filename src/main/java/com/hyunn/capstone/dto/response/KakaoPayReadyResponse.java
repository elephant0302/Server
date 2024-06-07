package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoPayReadyResponse {

  @Schema(type = "String", description = "결제 고유 번호", example = "T1234567890123456789")
  private String tid;

  @Schema(type = "String", description = "요청한 클라이언트가 모바일 웹일 경우\n" +
      "카카오톡 결제 페이지 Redirect URL", example = "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/mInfo")
  private String next_redirect_mobile_url;

  @Schema(type = "String", description = "요청한 클라이언트가 PC 웹일 경우\n" +
      "카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력 화면 Redirect URL", example = "https://mockup-pg-web.kakao.com/v1/xxxxxxxxxx/info")
  private String next_redirect_pc_url;

  @Schema(type = "String", description = "핸드폰 번호", example = "01012345678")
  private String partner_user_id;

  @Schema(type = "Long", description = "이미지 id", example = "1")
  private Long imageId;

  public KakaoPayReadyResponse(String tid, String next_redirect_mobile_url,
      String next_redirect_pc_url, String partner_user_id, Long imageId) {
    this.tid = tid;
    this.next_redirect_mobile_url = next_redirect_mobile_url;
    this.next_redirect_pc_url = next_redirect_pc_url;
    this.partner_user_id = partner_user_id;
    this.imageId = imageId;
  }

  public static KakaoPayReadyResponse create() {
    return new KakaoPayReadyResponse("null", "null", "null", "null", 0L);
  }

  public static KakaoPayReadyResponse create(String tid, String next_redirect_mobile_url,
      String next_redirect_pc_url, String partner_user_id, Long imageId) {
    return new KakaoPayReadyResponse(tid, next_redirect_mobile_url, next_redirect_pc_url,
        partner_user_id, imageId);
  }
}