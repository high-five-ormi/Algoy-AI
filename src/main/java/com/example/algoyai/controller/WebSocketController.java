package com.example.algoyai.controller;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

	private final ChatService chatService;

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/messages")
	public ChatMessageDto sendMessage(ChatMessageDto chatMessageDto) {
		chatService.sendMessage(chatMessageDto);
		return chatMessageDto;
	}
}