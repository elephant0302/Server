package com.hyunn.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public abstract class BaseEntity {

  // 생성 시간
  @CreatedDate
  @Column(name = "date", nullable = false, updatable = false)
  private LocalDateTime date;

  BaseEntity() {
    // 한국 시간으로 설정
    ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
    date = ZonedDateTime.now(koreaZoneId).toLocalDateTime();
  }

}
