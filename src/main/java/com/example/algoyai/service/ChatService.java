package com.example.algoyai.service;

import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ChannelTopic topic;

	public void sendMessage(String username, String content) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setUsername(username);
		chatMessage.setTimestamp(LocalDateTime.now());
		chatMessage.setContent(content);
		chatMessageRepository.save(chatMessage);

		redisTemplate.convertAndSend(topic.getTopic(), content);
	}
}