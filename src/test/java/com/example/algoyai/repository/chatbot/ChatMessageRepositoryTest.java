package com.example.algoyai.repository.chatbot;

import com.example.algoyai.model.entity.chatbot.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author JSW
 * ChatMessageRepository의 동작을 검증하는 테스트 클래스입니다.
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.mongodb.embedded.version=4.6.1",
    "spring.data.mongodb.database=testdb"
})
public class ChatMessageRepositoryTest {

  @Autowired private ChatMessageRepository chatMessageRepository;

  /** 각 테스트 실행 전에 저장소를 비웁니다. */
  @BeforeEach
  public void setUp() {
    chatMessageRepository.deleteAll().block(); // 저장소 초기화
  }

  /** 채팅 메시지를 저장하고, 올바르게 저장되었는지 확인하는 테스트입니다. */
  @Test
  public void testSaveChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage =
        ChatMessage.builder()
            .content("Hello, AI!")
            .responses(Arrays.asList("Hello, Human!"))
            .timestamp(LocalDateTime.now())
            .build();

    // When: 저장소에 ChatMessage 저장
    Mono<ChatMessage> savedMessage = chatMessageRepository.save(chatMessage);

    // Then: 저장된 메시지가 예상대로 저장되었는지 검증
    StepVerifier.create(savedMessage)
        .expectNextMatches(
            saved ->
                saved.getId() != null
                    && saved.getContent().equals("Hello, AI!")
                    && saved.getResponses().get(0).equals("Hello, Human!"))
        .verifyComplete();
  }

  /**
   * 모든 채팅 메시지를 조회하는 테스트입니다.
   */
  @Test
  public void testFindAllChatMessages() {
    // Given: 두 개의 ChatMessage 객체 생성
    ChatMessage message1 =
        ChatMessage.builder()
            .content("Message 1")
            .responses(Arrays.asList("Response 1"))
            .timestamp(LocalDateTime.now())
            .build();

    ChatMessage message2 =
        ChatMessage.builder()
            .content("Message 2")
            .responses(Arrays.asList("Response 2"))
            .timestamp(LocalDateTime.now())
            .build();

    // When: 두 메시지를 저장소에 저장
    chatMessageRepository.saveAll(Arrays.asList(message1, message2)).blockLast();

    // Then: 저장된 모든 메시지를 조회하고 개수를 검증
    Flux<ChatMessage> allMessages = chatMessageRepository.findAll();
    StepVerifier.create(allMessages).expectNextCount(2).verifyComplete();
  }

  /**
   * ID로 채팅 메시지를 조회하는 테스트입니다.
   */
  @Test
  public void testFindChatMessageById() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage =
        ChatMessage.builder()
            .content("Find me!")
            .responses(Arrays.asList("Found you!"))
            .timestamp(LocalDateTime.now())
            .build();

    // When: 메시지를 저장하고, 해당 메시지의 ID를 통해 조회
    String id = chatMessageRepository.save(chatMessage).block().getId();
    Mono<ChatMessage> foundMessage = chatMessageRepository.findById(id);

    // Then: 조회된 메시지가 올바른지 검증
    StepVerifier.create(foundMessage)
        .expectNextMatches(
            found -> found.getId().equals(id) && found.getContent().equals("Find me!"))
        .verifyComplete();
  }

  /**
   * 채팅 메시지를 업데이트하는 테스트입니다.
   */
  @Test
  public void testUpdateChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage =
        ChatMessage.builder()
            .content("Original content")
            .responses(Arrays.asList("Original response"))
            .timestamp(LocalDateTime.now())
            .build();

    // When: 메시지를 저장한 후 응답을 업데이트
    String id = chatMessageRepository.save(chatMessage).block().getId();

    Mono<ChatMessage> updatedMessage =
        chatMessageRepository
            .findById(id)
            .map(
                message -> {
                  message.appendResponse("Updated response");
                  return message;
                })
            .flatMap(chatMessageRepository::save);

    // Then: 업데이트된 메시지가 올바른지 검증
    StepVerifier.create(updatedMessage)
        .expectNextMatches(
            updated ->
                updated.getId().equals(id)
                    && updated.getResponses().size() == 2
                    && updated.getResponses().get(1).equals("Updated response"))
        .verifyComplete();
  }

  /**
   * 채팅 메시지를 삭제하는 테스트입니다.
   */
  @Test
  public void testDeleteChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage =
        ChatMessage.builder()
            .content("Delete me")
            .responses(Arrays.asList("Goodbye!"))
            .timestamp(LocalDateTime.now())
            .build();

    // When: 메시지를 저장한 후 해당 메시지를 삭제
    String id = chatMessageRepository.save(chatMessage).block().getId();
    Mono<Void> deleteResult = chatMessageRepository.deleteById(id);

    // Then: 삭제된 메시지를 검증
    StepVerifier.create(deleteResult).verifyComplete();
    Mono<ChatMessage> findResult = chatMessageRepository.findById(id);
    StepVerifier.create(findResult).expectNextCount(0).verifyComplete();
  }
}