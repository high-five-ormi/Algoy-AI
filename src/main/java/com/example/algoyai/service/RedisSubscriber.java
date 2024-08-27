package com.example.algoyai.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber {

	private final SimpMessagingTemplate messagingTemplate;

	public RedisSubscriber(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void handleMessage(String message) {
		messagingTemplate.convertAndSend("/topic/messages", message);
	}
}