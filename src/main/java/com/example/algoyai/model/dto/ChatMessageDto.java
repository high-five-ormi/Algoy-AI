package com.example.algoyai.model.dto;

import com.example.algoyai.model.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @author JSW
 *
 * 채팅 메시지의 데이터 전송 객체(DTO) 클래스입니다.
 * 엔티티와 DTO 간의 변환을 위한 메서드를 제공합니다.
 */
@Data
@Builder
public class ChatMessageDto {
	private String id;
	private String username;
	private String content;
	private LocalDateTime createdAt;

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
	 * @return 변환된 ChatMessage 엔티티 객체입니다.
	 */
	public ChatMessage toEntity() {
		return ChatMessage.builder()
			.id(this.id)
			.username(this.username)
			.content(this.content)
			.createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
			.build();
	}
}