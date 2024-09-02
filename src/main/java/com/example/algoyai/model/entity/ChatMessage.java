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

/**
 * @author JSW
 *
 * ChatMessage 클래스는 MongoDB 컬렉션 "chat_messages"에 저장되는 챗봇 메시지를 표현하는 엔티티 클래스입니다.
 * 이 클래스는 메시지의 ID, 내용(content), 응답 목록(responses), 타임스탬프(timestamp) 등을 포함합니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {

	@Id
	private String id;	// 메시지의 고유 식별자
	private String content;	// 메시지의 내용
	@Builder.Default
	private List<String> responses = new ArrayList<>();	// 이 메시지에 대한 응답 목록
	private LocalDateTime timestamp;	// 메시지가 생성된 시간 또는 저장된 시간

	/**
	 * 새로운 응답을 기존 응답 목록에 추가합니다.
	 *
	 * @param response 추가할 응답 내용
	 * @return 이 ChatMessage 객체 자신을 반환하여 메서드 체이닝을 지원합니다.
	 */
	public ChatMessage appendResponse(String response) {
		this.responses.add(response);
		return this;
	}

	/**
	 * 응답 목록에서 가장 마지막에 추가된 응답을 반환합니다.
	 *
	 * @return 응답 목록에서 가장 최근의 응답. 만약 응답 목록이 비어있다면 빈 문자열("")을 반환합니다.
	 */
	public String getLastResponse() {
		return responses.isEmpty() ? "" : responses.get(responses.size() - 1);
	}
}