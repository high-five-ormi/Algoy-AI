package com.example.algoyai.controller;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.service.ChatMessageService;
import com.example.algoyai.util.ChatDtoConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author JSW
 *
 * 채팅 메시지를 처리하는 컨트롤러 클래스입니다.
 * 클라이언트로부터 메시지를 받아 처리하고, 해당 메시지를 브로드캐스트합니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/ai")
public class ChatController {

  private final ChatMessageService chatMessageService;

  /**
   * 클라이언트로부터 "/chat" 경로로 오는 메시지를 처리합니다.
   *
   * @param messageDto 클라이언트로부터 전달받은 ChatMessageDto 객체입니다.
   * @return 처리된 메시지를 "/topic/messages" 경로로 브로드캐스트합니다.
   */
  @MessageMapping("/chat")
  @SendTo("/topic/messages")
  public ChatMessageDto send(ChatMessageDto messageDto) {
    return ChatDtoConvert.fromEntity(
        chatMessageService.saveMessage(messageDto.getUsername(), messageDto.getContent()));
  }
}