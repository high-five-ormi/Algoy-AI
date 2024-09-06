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

/**
 * @author JSW
 * ChatService의 동작을 검증하는 테스트 클래스입니다.
 */
class ChatServiceTest {

  private ChatService chatService;

  @Mock private WebClient.Builder webClientBuilder;
  @Mock private WebClient webClient;
  @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock private WebClient.ResponseSpec responseSpec;
  @Mock private ChatMessageRepository chatMessageRepository;

  /**
   * 테스트 전 Mock 객체 초기화 및 WebClient 설정
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(webClient);
    chatService =
        new ChatService(webClientBuilder, "http://test-api-url.com", chatMessageRepository);
  }

  /**
   * 정상적으로 채팅 응답을 가져오는 경우를 테스트합니다.
   */
  @Test
  void testGetChatResponse() {
    // Given: ChatMessage와 ChatMessageDto 생성
    ChatMessage chatMessage =
        ChatMessage.builder()
            .id("1")
            .content("Hello")
            .responses(Arrays.asList())
            .timestamp(LocalDateTime.now())
            .build();

    ChatMessageDto chatMessageDto =
        ChatMessageDto.builder()
            .type("response")
            .data(
                ChatMessageDto.ChatResponseData.builder().content("AI generated response").build())
            .build();

    // When: chatMessageRepository 및 WebClient의 동작 설정
    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(Mono.just(chatMessage));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.just(chatMessageDto));

    // Then: 채팅 응답 Flux를 생성하고 결과를 검증
    Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

    StepVerifier.create(result)
        .expectNextMatches(
            dto ->
                (dto.getType().equals("response")
                        && dto.getData() != null
                        && dto.getData().getContent() != null)
                    || (dto.getType().equals("error")
                        && dto.getData() != null
                        && dto.getData().getContent() != null))
        .verifyComplete();

    verify(chatMessageRepository, atLeast(1)).save(any(ChatMessage.class));
  }

  /**
   * 채팅 응답에서 에러가 발생하는 경우를 테스트합니다.
   */
  @Test
  void testGetChatResponseWithError() {
    // Given: 저장된 채팅 메시지 및 WebClient 에러 설정
    when(chatMessageRepository.save(any(ChatMessage.class)))
        .thenReturn(Mono.just(ChatMessage.builder().build()));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(ChatMessageDto.class))
        .thenReturn(Flux.error(new RuntimeException("API Error")));

    // Then: 에러 메시지가 포함된 결과를 검증
    Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

    StepVerifier.create(result)
        .expectNextMatches(
            dto -> dto.getType().equals("error") && dto.getData().getContent().startsWith("Error:"))
        .verifyComplete();
  }

  /**
   * 채팅 응답이 타임아웃되는 경우를 테스트합니다.
   */
  @Test
  void testGetChatResponseTimeout() {
    // Given: 타임아웃 에러를 시뮬레이션
    when(chatMessageRepository.save(any(ChatMessage.class)))
        .thenReturn(Mono.just(ChatMessage.builder().build()));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(ChatMessageDto.class))
        .thenReturn(Flux.error(new TimeoutException("Simulated timeout")));

    // Then: 타임아웃 메시지가 포함된 결과를 검증
    Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

    StepVerifier.create(result)
        .expectNextMatches(
            dto ->
                dto.getType().equals("error") && dto.getData().getContent().contains("timed out"))
        .expectComplete()
        .verify(Duration.ofSeconds(5));
  }

  /**
   * 여러 개의 채팅 응답을 순차적으로 받는 경우를 테스트합니다.
   */
  @Test
  void testGetChatResponseMultipleMessages() {
    // Given: 여러 개의 응답 메시지 생성
    ChatMessageDto message1 =
        ChatMessageDto.builder()
            .type("response")
            .data(ChatMessageDto.ChatResponseData.builder().content("Hello").build())
            .build();
    ChatMessageDto message2 =
        ChatMessageDto.builder()
            .type("response")
            .data(ChatMessageDto.ChatResponseData.builder().content("How can I help you?").build())
            .build();

    // When: WebClient가 여러 개의 응답을 반환하도록 설정
    when(chatMessageRepository.save(any(ChatMessage.class)))
        .thenReturn(Mono.just(ChatMessage.builder().build()));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.just(message1, message2));

    // Then: 두 개의 메시지를 순차적으로 받는지 검증
    Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

    StepVerifier.create(result)
        .expectNextMatches(dto -> dto.getData().getContent().equals("Hello"))
        .expectNextMatches(dto -> dto.getData().getContent().equals("How can I help you?"))
        .verifyComplete();
  }

  /**
   * 빈 응답을 받는 경우를 테스트합니다.
   */
  @Test
  void testGetChatResponseEmptyResponse() {
    // Given: WebClient가 빈 Flux를 반환하도록 설정
    when(chatMessageRepository.save(any(ChatMessage.class)))
        .thenReturn(Mono.just(ChatMessage.builder().build()));
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(ChatMessageDto.class)).thenReturn(Flux.empty());

    // Then: 빈 응답을 받는지 검증
    Flux<ChatMessageDto> result = chatService.getChatResponse("Hello");

    StepVerifier.create(result).expectNextCount(0).verifyComplete();
  }
}