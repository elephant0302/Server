package com.hyunn.capstone.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.capstone.dto.Request.ImageRequest;
import com.hyunn.capstone.dto.Response.ImageResponse;
import com.hyunn.capstone.service.ImageService;
import com.hyunn.capstone.service.PressoApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

  public final ImageService imageService;
  public final PressoApiService pressoApiService;

  // 프론트 -> 스프링 -> 플라스크 -> 스프링 -> presso -> 프론트
  //    create              result            ???
  @PostMapping("/create")
  public void imageToText(
      @Valid @RequestBody ImageRequest imageRequest) {
    // flask 서버로 전달한다.

  }

  @PostMapping("/result/{keyWord}")
  public ImageResponse textTo3D(
      @PathVariable String keyWord, @Valid @RequestBody ImageRequest imageRequest)
      throws JsonProcessingException, InterruptedException {
    return pressoApiService.textTo3D(imageRequest, keyWord);
  }


}
