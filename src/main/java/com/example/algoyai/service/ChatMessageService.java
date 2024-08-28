package com.example.algoyai.service;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author JSW
 *
 * 채팅 메시지와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * ChatMessageRepository를 사용하여 데이터베이스와 상호작용합니다.
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  /**
   * 새로운 채팅 메시지를 저장합니다.
   *
   * @param username 메시지를 보낸 사용자의 이름입니다.
   * @param content 메시지의 내용입니다.
   * @return 저장된 ChatMessage 엔티티 객체입니다.
   */
  public ChatMessage saveMessage(String username, String content) {
    ChatMessage message =
        ChatMessage.builder()
            .username(username)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();
    return chatMessageRepository.save(message);
  }

  /**
   * 모든 채팅 메시지를 조회합니다.
   *
   * @return 모든 ChatMessage 엔티티 객체의 리스트입니다.
   */
  public List<ChatMessage> getAllMessages() {
    return chatMessageRepository.findAll();
  }
}