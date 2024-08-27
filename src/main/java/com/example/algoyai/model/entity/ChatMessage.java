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
	private String username;
	private LocalDateTime timestamp;
	private String content;
}