package com.example.algoyai.service.chatbot;

import com.example.algoyai.model.dto.chatbot.ChatMessageDto;
import com.example.algoyai.model.entity.chatbot.ChatMessage;
import com.example.algoyai.repository.chatbot.ChatMessageRepository;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceTest {

	private ChatService chatService;

	@Mock
	private WebClient.Builder webClientBuilder;

	@Mock
	private WebClient webClient;

	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	@Mock
	private ChatMessageRepository chatMessageRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
		when(webClientBuilder.build()).thenReturn(webClient);
		chatService = new ChatService(webClientBuilder, "http://test-api-url.com", chatMessageRepository);
	}

	@Test
	void testGetChatResponse() {
		ChatMessage chatMessage = ChatMessage.builder()
			.id("1")
			.content("Hello")
			.responses(Arrays.asList())
			.timestamp(LocalDateTime.now())
			.build();

		ChatMessageDto chatMessageDto = ChatMessageDto.builder()
			.type("response")
			.data(ChatMessageDto.ChatResponseData.builder()
				.content("AI generated response")
				.build())
			.build();

		when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(chatMessage));
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.just(chatMessageDto));

		Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

		StepVerifier.create(result)
			.expectNextMatches(dto ->
				(dto.getType().equals("response") && dto.getData() != null && dto.getData().getContent() != null) ||
					(dto.getType().equals("error") && dto.getData() != null && dto.getData().getContent() != null)
			)
			.verifyComplete();

		verify(chatMessageRepository, atLeast(1)).save(any(ChatMessage.class));
	}

	@Test
	void testGetChatResponseWithError() {
		when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(ChatMessage.builder().build()));
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.error(new RuntimeException("API Error")));

		Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

		StepVerifier.create(result)
			.expectNextMatches(dto -> dto.getType().equals("error") &&
				dto.getData().getContent().startsWith("Error:"))
			.verifyComplete();
	}

	@Test
	void testGetChatResponseTimeout() {
		when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(ChatMessage.builder().build()));
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

		// 타임아웃 시뮬레이션
		when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.error(new TimeoutException("Simulated timeout")));

		Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

		StepVerifier.create(result)
			.expectNextMatches(dto -> dto.getType().equals("error") &&
				dto.getData().getContent().contains("timed out"))
			.expectComplete()
			.verify(Duration.ofSeconds(5));
	}

	@Test
	void testGetChatResponseMultipleMessages() {
		ChatMessageDto message1 = ChatMessageDto.builder()
			.type("response")
			.data(ChatMessageDto.ChatResponseData.builder()
				.content("Hello")
				.build())
			.build();
		ChatMessageDto message2 = ChatMessageDto.builder()
			.type("response")
			.data(ChatMessageDto.ChatResponseData.builder()
				.content("How can I help you?")
				.build())
			.build();

		when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(ChatMessage.builder().build()));
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.just(message1, message2));

		Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

		StepVerifier.create(result)
			.expectNextMatches(dto -> dto.getData().getContent().equals("Hello"))
			.expectNextMatches(dto -> dto.getData().getContent().equals("How can I help you?"))
			.verifyComplete();
	}

	@Test
	void testGetChatResponseEmptyResponse() {
		when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(ChatMessage.builder().build()));
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.empty());

		Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

		StepVerifier.create(result)
			.expectNextCount(0)
			.verifyComplete();
	}
}