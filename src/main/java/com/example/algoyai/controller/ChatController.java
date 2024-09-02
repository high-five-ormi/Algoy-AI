package com.example.algoyai.controller;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.service.ChatService;
import com.example.algoyai.util.InputSanitizer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author JSW
 *
 * ChatController 클래스는 AI 챗봇과의 상호작용을 처리하는 REST 컨트롤러입니다.
 * 이 컨트롤러는 "/ai/api/chat" 경로에 대한 HTTP 요청을 처리하며, 주로 스트리밍 형태로
 * AI의 응답을 반환하는 기능을 제공합니다.
 */
@RestController
@RequestMapping("/ai/api/chat")
public class ChatController {

	private final ChatService chatService;

	/**
	 * ChatController의 생성자입니다. ChatService를 주입받아 초기화합니다.
	 *
	 * @param chatService 챗봇 응답을 처리하는 서비스 클래스
	 */
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	/**
	 * AI의 챗봇 응답을 스트리밍 방식으로 제공하는 메서드입니다.
	 * 사용자가 입력한 메시지를 받아서 이를 처리한 후, AI의 응답을 실시간으로 스트리밍합니다.
	 *
	 * @param content 사용자가 입력한 메시지 내용
	 * @return AI의 응답을 실시간으로 스트리밍하는 Flux<ChatMessageDto> 객체
	 */
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ChatMessageDto> streamChatResponse(@RequestParam String content) {
		String sanitizedContent = InputSanitizer.sanitize(content);	// 입력된 내용을 안전하게 처리
		return chatService.getChatResponse(sanitizedContent);	// 처리된 내용을 바탕으로 AI 응답 스트리밍 시작
	}
}