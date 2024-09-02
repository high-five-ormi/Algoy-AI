package com.example.algoyai.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
	@Builder.Default
	private List<String> responses = new ArrayList<>();
	private LocalDateTime timestamp;

	public ChatMessage appendResponse(String response) {
		this.responses.add(response);
		return this;
	}

	public String getLastResponse() {
		return responses.isEmpty() ? "" : responses.get(responses.size() - 1);
	}
}