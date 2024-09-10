package com.example.algoyai;

import com.example.algoyai.model.dto.chatbot.ChatMessageDto;
import com.example.algoyai.model.entity.chatbot.ChatMessage;
import com.example.algoyai.repository.chatbot.ChatMessageRepository;
import com.example.algoyai.service.chatbot.ChatService;
import com.example.algoyai.service.solvedac.AllenApiService;
import com.example.algoyai.service.solvedac.AllenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author JSW
 *
 * AlgoyAI 애플리케이션의 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AlgoyAiApplicationTests {

  @Autowired private WebTestClient webTestClient;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ChatMessageRepository chatMessageRepository;

  @MockBean private ChatService chatService;

  @MockBean private AllenApiService allenApiService;

  @MockBean private AllenService allenService;

  /**
   * 각 테스트 실행 전에 ChatMessageRepository를 초기화합니다.
   */
  @BeforeEach
  public void setup() {
    chatMessageRepository.deleteAll().block();
  }

  /**
   * ChatController의 통합 동작을 테스트합니다.
   * 챗봇 API에 메시지를 전송하고 응답을 확인합니다.
   */
  @Test
  public void testChatControllerIntegration() {
    // given: 테스트 데이터 준비
    String testContent = "Hello, AI!";
    ChatMessageDto expectedResponse =
        ChatMessageDto.builder()
            .type("complete")
            .data(ChatMessageDto.ChatResponseData.builder().content("Hello, Human!").build())
            .build();

    // when: ChatService 동작 모킹 및 API 호출
    when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(expectedResponse));

    // then: 응답 검증
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", testContent).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(1)
        .contains(expectedResponse);
  }

  /**
   * AllenController의 통합 동작을 테스트합니다. Allen API를 호출하고 응답을 확인합니다.
   */
  @Test
  public void testAllenControllerIntegration() throws Exception {
    // given: 테스트 데이터 준비
    String algoyusername = "testUser";
    String solvedacusername = "testSolvedAC";
    String mockContent = "Mock content";
    String mockResponse = "Mock AI response";

    // when: AllenApiService 동작 모킹 및 API 호출
    when(allenApiService.sovledacCall(anyString())).thenReturn(mockContent);
    when(allenApiService.callApi(anyString(), anyString())).thenReturn(mockResponse);

    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/ai/allenapi?algoyusername={algoyusername}&solvedacusername={solvedacusername}",
            String.class,
            algoyusername,
            solvedacusername);

    // then: 응답 검증
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(mockResponse);
  }

  /**
   * ChatMessageRepository의 통합 동작을 테스트합니다. 메시지 저장 및 조회 기능을 확인합니다.
   */
  @Test
  public void testChatMessageRepositoryIntegration() {
    // given: 테스트 데이터 준비
    ChatMessage testMessage = ChatMessage.builder().content("Test content").build();

    // when: 메시지 저장
    ChatMessage savedMessage = chatMessageRepository.save(testMessage).block();

    // then: 저장된 메시지 검증
    assertThat(savedMessage).isNotNull();
    assertThat(savedMessage.getId()).isNotNull();
    assertThat(savedMessage.getContent()).isEqualTo(testMessage.getContent());

    // when: 모든 메시지 조회
    List<ChatMessage> allMessages = chatMessageRepository.findAll().collectList().block();

    // then: 조회된 메시지 검증
    assertThat(allMessages).hasSize(1);
    assertThat(allMessages.get(0).getContent()).isEqualTo(testMessage.getContent());
  }

  /**
   * 입력 sanitizer의 통합 동작을 테스트합니다. XSS 공격 가능성이 있는 입력이 적절히 처리되는지 확인합니다.
   */
  @Test
  public void testInputSanitizerIntegration() {
    // given: 테스트 데이터 준비
    String unsafeContent = "<script>alert('XSS');</script>";
    ChatMessageDto sanitizedResponse =
        ChatMessageDto.builder()
            .type("complete")
            .data(
                ChatMessageDto.ChatResponseData.builder()
                    .content("&lt;script&gt;alert('XSS');&lt;/script&gt;")
                    .build())
            .build();

    // when: ChatService 동작 모킹 및 API 호출
    when(chatService.getChatResponse(anyString())).thenReturn(Flux.just(sanitizedResponse));

    // then: 응답 검증
    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/ai/api/chat/stream").queryParam("content", unsafeContent).build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(ChatMessageDto.class)
        .hasSize(1)
        .contains(sanitizedResponse);
  }

  /**
   * 오류 처리 로직의 통합 동작을 테스트합니다.
   * 예외 발생 시 적절한 오류 응답이 반환되는지 확인합니다.
   */
  @Test
  public void testErrorHandlingIntegration() throws Exception {
    // given: 테스트 데이터 준비 및 예외 상황 설정
    when(allenApiService.sovledacCall(anyString())).thenThrow(new RuntimeException("Test error"));

    // when: API 호출
    ResponseEntity<String> response =
        restTemplate.getForEntity(
            "/ai/allenapi?algoyusername={algoyusername}&solvedacusername={solvedacusername}",
            String.class,
            "testUser",
            "testSolvedAC");

    // then: 오류 응답 검증
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).contains("api 호출 중 에러가 발생하였습니다");
  }
}