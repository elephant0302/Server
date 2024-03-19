package com.hyunn.capstone.controller.config;

import com.hyunn.capstone.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserCleanUpScheduler {

  private UserService userService;

  // 매일 자정에 실행
  @Scheduled(cron = "0 0 0 * * *")
  public void deleteInactiveUsersJob() {
    userService.deleteInactiveUsers();
  }
}
