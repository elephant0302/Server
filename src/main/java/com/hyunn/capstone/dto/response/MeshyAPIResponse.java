package com.hyunn.capstone.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;

@Getter
public class MeshyAPIResponse {

  @Schema(type = "Long", description = "이미지 id", example = "1")
  private Long imageId;

  @Schema(type = "String", description = "이미지", example = "human_face.img")
  private String image;

  @Schema(type = "Map", description = "3D 모델을 받을 수 있는 다양한 형식의 URL",
      example = "{\n"
          + "            \"glb\": \"https://assets.meshy.ai/**\",\n"
          + "            \"fbx\": \"https://assets.meshy.ai/**\",\n"
          + "            \"usdz\": \"https://assets.meshy.ai/**\",\n"
          + "            \"obj\": \"https://assets.meshy.ai/**\",\n"
          + "            \"mtl\": \"https://assets.meshy.ai/**\"\n"
          + "        }")
  private Map<String, String> threeDimensionUrl;

  @Schema(type = "String", description = "flask 서버에서 반환한 동물 키워드", example = "고양이")
  private String keyWord;

  public MeshyAPIResponse(Long imageId, String image, Map<String, String> threeDimensionUrl,
      String keyWord) {
    this.imageId = imageId;
    this.image = image;
    this.threeDimensionUrl = threeDimensionUrl;
    this.keyWord = keyWord;
  }

  public static MeshyAPIResponse create(Long imageId, String image,
      Map<String, String> threeDimensionUrl, String keyWord) {
    return new MeshyAPIResponse(imageId, image, threeDimensionUrl, keyWord);
  }
}
