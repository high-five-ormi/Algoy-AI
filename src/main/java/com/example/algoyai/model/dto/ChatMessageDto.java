package com.example.algoyai.model.dto;

import com.example.algoyai.model.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageDto {
	private String username;
	private LocalDateTime timestamp;
	private String content;

	public static ChatMessageDto fromEntity(ChatMessage chatMessage) {
		return ChatMessageDto.builder()
			.username(chatMessage.getUsername())
			.timestamp(chatMessage.getTimestamp())
			.content(chatMessage.getContent())
			.build();
	}

	public ChatMessage toEntity() {
		return ChatMessage.builder()
			.username(this.username)
			.timestamp(this.timestamp != null ? this.timestamp : LocalDateTime.now())
			.content(this.content)
			.build();
	}
}