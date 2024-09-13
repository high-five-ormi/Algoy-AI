package com.example.algoyai.util.chatbot;

import com.example.algoyai.model.dto.chatbot.ChatMessageDto;
import com.example.algoyai.model.entity.chatbot.ChatMessage;

/**
 * @author JSW
 *
 * ChatMessageMapper 클래스는 ChatMessage 엔티티와 ChatMessageDto 사이의 변환 작업을 수행합니다.
 * 이 클래스는 주로 엔티티와 DTO 간의 변환을 쉽게 하기 위해 사용됩니다.
 */
public class ChatMessageMapper {

  /**
   * ChatMessage 엔티티를 ChatMessageDto로 변환합니다.
   *
   * @param chatMessage 변환할 ChatMessage 엔티티
   * @return 변환된 ChatMessageDto
   */
  public static ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .type("complete")
        .data(ChatMessageDto.ChatResponseData.builder()
            .content(chatMessage.getLastResponse())
            .build())
        .build();
  }

  /**
   * ChatMessageDto를 ChatMessage 엔티티로 변환합니다.
   *
   * @param chatMessageDto 변환할 ChatMessageDto
   * @return 변환된 ChatMessage 엔티티
   */
  public static ChatMessage toEntity(ChatMessageDto chatMessageDto) {
    return ChatMessage.builder()
        .content(chatMessageDto.getData().getContent())
        .responses(java.util.Collections.singletonList(chatMessageDto.getData().getContent()))
        .timestamp(java.time.LocalDateTime.now())
        .build();
  }

  /**
   * 기존의 ChatMessage 엔티티를 업데이트합니다.
   * 주어진 ChatMessageDto의 데이터를 사용하여 기존 엔티티에 응답을 추가합니다.
   *
   * @param chatMessage 업데이트할 ChatMessage 엔티티
   * @param chatMessageDto 업데이트에 사용할 ChatMessageDto
   * @return 업데이트된 ChatMessage 엔티티
   */
  public static ChatMessage updateEntity(ChatMessage chatMessage, ChatMessageDto chatMessageDto) {
    chatMessage.appendResponse(chatMessageDto.getData().getContent());
    return chatMessage;
  }
}