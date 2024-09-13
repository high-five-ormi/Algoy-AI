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

/**
 * @author JSW
 * ChatController의 동작을 검증하는 테스트 클래스입니다.
 */
@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  @Mock private ChatService chatService;

  @InjectMocks private ChatController chatController;
  private WebTestClient webTestClient;

  /** 테스트 전에 WebTestClient를 ChatController에 바인딩하여 초기 설정을 수행합니다. */
  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToController(chatController).build();
  }

  /**
   * 채팅 메시지를 정상적으로 처리하고 단일 응답을 반환하는 경우의 테스트입니다. "Hello, AI!" 메시지를 보내면, AI가 "Hello, Human!"으로 응답하는 것을
   * 검증합니다.
   */
  @Test
  void testStreamChatResponse_Success() {
    // Given: 입력 메시지와 기대하는 응답 설정
    String content = "Hello, AI!";
    ChatMessageDto responseDto =
        new ChatMessageDto("complete", new ChatMessageDto.ChatResponseData("Hello, Human!", "AI"));

    // When: ChatService의 getChatResponse가 특정 값을 반환하도록 모킹
    when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(responseDto));

    // Then: API 호출 결과가 예상과 일치하는지 검증
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", content).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(1)
        .contains(responseDto);
  }

  /** 빈 문자열을 콘텐츠로 보냈을 때 응답이 없는지 확인하는 테스트입니다. */
  @Test
  void testStreamChatResponse_EmptyContent() {
    // Given: 빈 문자열 콘텐츠
    String content = "";

    // When: 빈 콘텐츠로 API 호출
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", content).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()

        // Then: 응답 메시지가 없음을 확인
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(0);
  }

  /** 매우 긴 콘텐츠를 보냈을 때 응답을 정상적으로 처리하는지 확인하는 테스트입니다. */
  @Test
  void testStreamChatResponse_LongContent() {
    // Given: 매우 긴 문자열 콘텐츠와 기대되는 응답 설정
    String content = "This is a very long message. ".repeat(100);
    ChatMessageDto responseDto =
        new ChatMessageDto(
            "complete", new ChatMessageDto.ChatResponseData("Response to long message", "AI"));

    // When: ChatService가 긴 메시지에 대한 응답을 반환하도록 모킹
    when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(responseDto));

    // Then: API 호출 결과가 예상과 일치하는지 검증
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", content).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(1)
        .contains(responseDto);
  }

  /**
   * 여러 개의 메시지를 순차적으로 응답받는 경우를 테스트합니다. 첫 번째 응답은 "Once upon a time", 두 번째 응답은 "there was a brave
   * programmer."입니다.
   */
  @Test
  void testStreamChatResponse_MultipleMessages() {
    // Given: 입력 메시지와 두 개의 예상 응답 설정
    String content = "Tell me a story";
    ChatMessageDto response1 =
        new ChatMessageDto(
            "partial", new ChatMessageDto.ChatResponseData("Once upon a time", "AI"));
    ChatMessageDto response2 =
        new ChatMessageDto(
            "complete", new ChatMessageDto.ChatResponseData("there was a brave programmer.", "AI"));

    // When: ChatService가 순차적으로 응답을 반환하도록 모킹
    when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(response1, response2));

    // Then: API 호출 결과가 두 개의 응답을 순차적으로 받는지 검증
    Flux<ChatMessageDto> result =
        webTestClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path("/ai/api/chat/stream").queryParam("content", content).build())
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ChatMessageDto.class)
            .getResponseBody();

    StepVerifier.create(result).expectNext(response1).expectNext(response2).verifyComplete();
  }

  /** 입력 내용에 대해 XSS 등의 악의적인 스크립트가 포함된 경우, 이를 정상적으로 필터링(입력 정화)하여 응답하는지 테스트합니다. */
  @Test
  void testStreamChatResponse_InputSanitization() {
    // Given: 악성 스크립트가 포함된 입력 메시지와 정화된 콘텐츠 및 예상 응답 설정
    String content = "<script>alert('XSS');</script>";
    String sanitizedContent = InputSanitizer.sanitize(content);
    ChatMessageDto responseDto =
        new ChatMessageDto(
            "complete", new ChatMessageDto.ChatResponseData("Sanitized response", "AI"));

    // When: 정화된 콘텐츠에 대해 ChatService가 응답을 반환하도록 모킹
    when(chatService.getChatResponse(sanitizedContent)).thenReturn(Flux.just(responseDto));

    // Then: API 호출 결과가 예상대로 정화된 응답을 반환하는지 검증
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", content).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(1)
        .contains(responseDto);
  }
}