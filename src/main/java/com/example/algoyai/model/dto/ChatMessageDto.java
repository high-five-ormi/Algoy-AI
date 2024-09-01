package com.example.algoyai.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
	private String type;
	private ChatResponseData data;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatResponseData {
		private String content;
		private String name;
	}
}