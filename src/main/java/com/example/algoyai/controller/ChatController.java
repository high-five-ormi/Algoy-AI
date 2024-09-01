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

@RestController
@RequestMapping("/ai/api/chat")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ChatMessageDto> streamChatResponse(@RequestParam String content) {
		String sanitizedContent = InputSanitizer.sanitize(content);
		return chatService.getChatResponse(sanitizedContent);
	}
}