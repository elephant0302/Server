package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Request.ImageRequest;
import com.hyunn.capstone.dto.Response.ThreeDimensionCreateResponse;
import com.hyunn.capstone.dto.Response.ThreeDimensionResponse;
import com.hyunn.capstone.service.ImageService;
import com.hyunn.capstone.service.MeshyApiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

  public final ImageService imageService;
  public final MeshyApiService meshyApiService;

  // 프론트 -> 스프링 -> 플라스크 -> 스프링 -> meshy -> 프론트
  //        imageToText           textTo3D   return3D
  @PostMapping("/image_to_text")
  public void imageToText(
      @Valid @RequestBody ImageRequest imageRequest) {
    // flask 서버에 api가 존재해야함 -> 전달하면 키워드를 받는 식으로
  }

  @PostMapping("/text_to_3D/{keyWord}")
  public ThreeDimensionCreateResponse textTo3D(
      @PathVariable String keyWord, @Valid @RequestBody ImageRequest imageRequest)
      throws JsonProcessingException {
    return meshyApiService.textTo3D(imageRequest, keyWord);
  }

  @GetMapping("/result/{previewResult}")
  public ThreeDimensionResponse return3D(
      @PathVariable String previewResult) {
    return meshyApiService.return3D(previewResult);
  }

  @PostMapping("/refine/{previewResult}")
  public String refine3D(
      @PathVariable String previewResult)
      throws JsonProcessingException {
    return meshyApiService.refine3D(previewResult);
  }

}
