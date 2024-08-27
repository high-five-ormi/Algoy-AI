package com.example.algoyai.service;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.repository.ChatMessageRepository;
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

	public void sendMessage(ChatMessageDto chatMessageDto) {
		ChatMessage chatMessage = chatMessageDto.toEntity();
		chatMessageRepository.save(chatMessage);
		redisTemplate.convertAndSend(topic.getTopic(), chatMessageDto.getContent());
	}
}