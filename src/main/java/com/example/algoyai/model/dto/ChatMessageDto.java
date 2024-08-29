package com.example.algoyai.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatMessageDto {
	private String id;
	private String content;
	private String response;
	private LocalDateTime timestamp;
}