package com.example.algoyai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final SimpMessagingTemplate messagingTemplate;

	public void handleMessage(String message) {
		messagingTemplate.convertAndSend("/topic/messages", message);
	}
}