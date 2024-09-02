package com.example.algoyai.util;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;

public class ChatMessageMapper {

  public static ChatMessageDto toDto(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .type("complete")
        .data(ChatMessageDto.ChatResponseData.builder()
            .content(chatMessage.getLastResponse())
            .build())
        .build();
  }

  public static ChatMessage toEntity(ChatMessageDto chatMessageDto) {
    return ChatMessage.builder()
        .content(chatMessageDto.getData().getContent())
        .responses(java.util.Collections.singletonList(chatMessageDto.getData().getContent()))
        .timestamp(java.time.LocalDateTime.now())
        .build();
  }

  public static ChatMessage updateEntity(ChatMessage chatMessage, ChatMessageDto chatMessageDto) {
    chatMessage.appendResponse(chatMessageDto.getData().getContent());
    return chatMessage;
  }
}