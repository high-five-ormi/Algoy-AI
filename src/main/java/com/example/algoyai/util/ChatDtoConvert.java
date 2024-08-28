package com.example.algoyai.util;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;
import java.time.LocalDateTime;

/**
 * @author JSW
 *
 * 엔티티와 DTO 간의 변환을 위한 메서드를 제공합니다.
 */
public class ChatDtoConvert {
	/**
	 * ChatMessage 엔티티를 ChatMessageDto로 변환합니다.
	 *
	 * @param chatMessage 변환할 ChatMessage 엔티티 객체입니다.
	 * @return 변환된 ChatMessageDto 객체입니다.
	 */
	public static ChatMessageDto fromEntity(ChatMessage chatMessage) {
		return ChatMessageDto.builder()
			.id(chatMessage.getId())
			.username(chatMessage.getUsername())
			.content(chatMessage.getContent())
			.createdAt(chatMessage.getCreatedAt())
			.build();
	}

	/**
	 * ChatMessageDto를 ChatMessage 엔티티로 변환합니다.
	 *
	 * @param chatMessageDto 변환할 ChatMessageDto 객체입니다.
	 * @return 변환된 ChatMessage 엔티티 객체입니다.
	 */
	public static ChatMessage toEntity(ChatMessageDto chatMessageDto) {
		return ChatMessage.builder()
			.id(chatMessageDto.getId())
			.username(chatMessageDto.getUsername())
			.content(chatMessageDto.getContent())
			.createdAt(chatMessageDto.getCreatedAt() != null ? chatMessageDto.getCreatedAt() : LocalDateTime.now())
			.build();
	}
}