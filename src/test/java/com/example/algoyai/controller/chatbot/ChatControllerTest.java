package com.example.algoyai.controller.chatbot;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.algoyai.model.dto.chatbot.ChatMessageDto;
import com.example.algoyai.service.chatbot.ChatService;
import com.example.algoyai.util.InputSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

	@Mock
	private ChatService chatService;

	@InjectMocks
	private ChatController chatController;

	private WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToController(chatController).build();
	}

	@Test
	void testStreamChatResponse_Success() {
		String content = "Hello, AI!";
		ChatMessageDto responseDto = new ChatMessageDto("complete",
			new ChatMessageDto.ChatResponseData("Hello, Human!", "AI"));

		when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(responseDto));

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/ai/api/chat/stream")
				.queryParam("content", content)
				.build())
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ChatMessageDto.class)
			.hasSize(1)
			.contains(responseDto);
	}

	@Test
	void testStreamChatResponse_EmptyContent() {
		String content = "";

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/ai/api/chat/stream")
				.queryParam("content", content)
				.build())
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ChatMessageDto.class)
			.hasSize(0);
	}

	@Test
	void testStreamChatResponse_LongContent() {
		String content = "This is a very long message. ".repeat(100);
		ChatMessageDto responseDto = new ChatMessageDto("complete",
			new ChatMessageDto.ChatResponseData("Response to long message", "AI"));

		when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(responseDto));

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/ai/api/chat/stream")
				.queryParam("content", content)
				.build())
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ChatMessageDto.class)
			.hasSize(1)
			.contains(responseDto);
	}

	@Test
	void testStreamChatResponse_MultipleMessages() {
		String content = "Tell me a story";
		ChatMessageDto response1 = new ChatMessageDto("partial",
			new ChatMessageDto.ChatResponseData("Once upon a time", "AI"));
		ChatMessageDto response2 = new ChatMessageDto("complete",
			new ChatMessageDto.ChatResponseData("there was a brave programmer.", "AI"));

		when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(response1, response2));

		Flux<ChatMessageDto> result = webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/ai/api/chat/stream")
				.queryParam("content", content)
				.build())
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus().isOk()
			.returnResult(ChatMessageDto.class)
			.getResponseBody();

		StepVerifier.create(result)
			.expectNext(response1)
			.expectNext(response2)
			.verifyComplete();
	}

	@Test
	void testStreamChatResponse_InputSanitization() {
		String content = "<script>alert('XSS');</script>";
		String sanitizedContent = InputSanitizer.sanitize(content);
		ChatMessageDto responseDto = new ChatMessageDto("complete",
			new ChatMessageDto.ChatResponseData("Sanitized response", "AI"));

		when(chatService.getChatResponse(sanitizedContent)).thenReturn(Flux.just(responseDto));

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/ai/api/chat/stream")
				.queryParam("content", content)
				.build())
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(ChatMessageDto.class)
			.hasSize(1)
			.contains(responseDto);
	}
}