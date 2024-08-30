package com.example.algoyai.service;


import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.repository.ChatMessageRepository;
import com.example.algoyai.util.ChatMessageMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Service
public class ChatService {

	private final WebClient webClient;
	private final ChatMessageRepository chatMessageRepository;

	@Value("${ai.api.key}")
	private String apiKey;

	public ChatService(WebClient.Builder webClientBuilder,
		@Value("${ai.api.url}") String apiUrl,
		ChatMessageRepository chatMessageRepository) {
		this.webClient = webClientBuilder.baseUrl(apiUrl).build();
		this.chatMessageRepository = chatMessageRepository;
	}

	public Flux<ServerSentEvent<ChatMessageDto>> getChatResponse(String content) {
		ChatMessage initialChatMessage = ChatMessage.builder()
			.content(content)
			.timestamp(LocalDateTime.now())
			.response("")
			.build();
		ChatMessage savedChatMessage = chatMessageRepository.save(initialChatMessage);

		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("content", content)
				.queryParam("client_id", apiKey)
				.build())
			.retrieve()
			.bodyToFlux(String.class)
			.doOnError(e -> {
				System.err.println("Error occurred during API call: " + e.getMessage());
			})
			.map(eventData -> {
				ChatMessage updatedChatMessage = savedChatMessage.appendResponse(eventData);
				ChatMessage savedUpdatedChatMessage = chatMessageRepository.save(updatedChatMessage);
				ChatMessageDto chatMessageDto = ChatMessageMapper.toDto(savedUpdatedChatMessage);
				return ServerSentEvent.<ChatMessageDto>builder()
					.data(chatMessageDto)
					.build();
			})
			.timeout(Duration.ofSeconds(90))
			.onErrorResume(e -> {
				System.err.println("Timeout or error occurred: " + e.getMessage());
				ChatMessageDto errorDto = ChatMessageDto.builder()
					.content(content)
					.response("Error: " + e.getMessage())
					.timestamp(LocalDateTime.now())
					.build();
				return Flux.just(
					ServerSentEvent.<ChatMessageDto>builder()
						.data(errorDto)
						.build());
			});
	}
}