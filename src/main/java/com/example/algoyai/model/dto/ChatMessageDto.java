package com.example.algoyai.model.dto;

import com.example.algoyai.model.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * @author JSW
 *
 * 채팅 메시지의 데이터 전송 객체(DTO) 클래스입니다.
 */
@Data
@Builder
public class ChatMessageDto {
	private String id;
	private String username;
	private String content;
	private LocalDateTime createdAt;
}