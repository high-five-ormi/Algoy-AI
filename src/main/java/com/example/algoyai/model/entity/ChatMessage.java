package com.example.algoyai.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {
	@Id
	private String id;
	private String content;
	private String response;
	private LocalDateTime timestamp;

	public ChatMessage appendResponse(String response) {
		this.response = this.response + "\n" + response; // 응답 누적
		return this; // 메서드 체이닝을 위해 자신을 반환
	}
}