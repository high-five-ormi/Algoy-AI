package com.example.algoyai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author JSW
 *
 * 뷰를 반환하는 컨트롤러 클래스입니다.
 * 클라이언트의 요청에 따라 특정 뷰를 반환합니다.
 */
@Controller
public class ViewController {

  /**
   * "/chat" 경로로 GET 요청이 들어오면 "chat" 뷰를 반환합니다.
   *
   * @return "chat" 뷰의 이름을 반환합니다.
   */
  @GetMapping("/chat")
  public String chat() {
    return "chat";
  }
}