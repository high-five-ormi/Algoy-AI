package com.example.algoyai.model.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JSW
 *
 * ChatMessageDto 클래스는 AI 챗봇과의 대화에서 주고받는 메시지를 표현하는 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 메시지의 유형(type)과 실제 응답 데이터(data)를 포함합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

	private String type;	// 메시지의 유형
	private ChatResponseData data;	// 메시지의 실제 응답 데이터를 포함하는 객체

	/**
	 * @author JSW
	 *
	 * ChatResponseData 클래스는 챗봇 응답의 세부 데이터를 표현합니다.
	 * 이 클래스는 응답의 내용(content)과 응답을 보낸 사람의 이름(name)을 포함합니다.
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatResponseData {
		private String content;	// 응답 메시지의 내용
		private String name;	// 응답 메시지를 보낸 사람의 이름
	}
}