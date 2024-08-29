package com.example.algoyai.util;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;

public class ChatMessageMapper {

	public static ChatMessageDto toDto(ChatMessage chatMessage) {
		return ChatMessageDto.builder()
			.id(chatMessage.getId())
			.content(chatMessage.getContent())
			.response(chatMessage.getResponse())
			.timestamp(chatMessage.getTimestamp())
			.build();
	}

	public static ChatMessage toEntity(ChatMessageDto chatMessageDto) {
		return ChatMessage.builder()
			.id(chatMessageDto.getId())
			.content(chatMessageDto.getContent())
			.response(chatMessageDto.getResponse())
			.timestamp(chatMessageDto.getTimestamp())
			.build();
	}
}