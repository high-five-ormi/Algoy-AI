package com.example.algoyai.controller;

import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ai")
public class WebSocketController {

	private final ChatService chatService;

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/messages")
	public ChatMessage sendMessage(ChatMessage chatMessage) {
		chatService.sendMessage(chatMessage.getUsername(), chatMessage.getContent());
		return chatMessage;
	}
}