package com.example.algoyai.repository.chatbot;

import com.example.algoyai.model.entity.chatbot.ChatMessage;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author JSW
 * ChatMessageRepository의 동작을 검증하는 테스트 클래스입니다.
 */
@DataMongoTest
@AutoConfigureDataMongo
public class ChatMessageRepositoryTest {

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  /**
   * 각 테스트 실행 전에 저장소의 모든 데이터를 삭제합니다.
   */
  @BeforeEach
  public void setUp() {
    StepVerifier.create(chatMessageRepository.deleteAll())
        .verifyComplete(); // 저장소를 비운 후 테스트 진행
  }

  /**
   * 채팅 메시지를 저장하고, 저장된 메시지가 예상대로 저장되었는지 확인하는 테스트입니다.
   */
  @Test
  public void testSaveChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .content("Hello, AI!")
        .responses(Arrays.asList("Hello, Human!"))
        .timestamp(LocalDateTime.now())
        .build();

    // When & Then: 메시지를 저장하고, 저장된 데이터가 올바른지 검증
    StepVerifier.create(chatMessageRepository.save(chatMessage))
        .expectNextMatches(saved ->
            saved.getId() != null &&
                saved.getContent().equals("Hello, AI!") &&
                saved.getResponses().get(0).equals("Hello, Human!")
        )
        .verifyComplete();
  }

  /**
   * 모든 채팅 메시지를 조회하는 테스트입니다.
   */
  @Test
  public void testFindAllChatMessages() {
    // Given: 두 개의 ChatMessage 객체 생성
    ChatMessage message1 = ChatMessage.builder()
        .content("Message 1")
        .responses(Arrays.asList("Response 1"))
        .timestamp(LocalDateTime.now())
        .build();

    ChatMessage message2 = ChatMessage.builder()
        .content("Message 2")
        .responses(Arrays.asList("Response 2"))
        .timestamp(LocalDateTime.now())
        .build();

    // When: 두 메시지를 저장소에 저장한 후, 모든 메시지를 조회
    StepVerifier.create(chatMessageRepository.saveAll(Arrays.asList(message1, message2))
            .thenMany(chatMessageRepository.findAll())) // 저장 후 조회
        .expectNextCount(2) // 두 개의 메시지를 기대
        .verifyComplete();
  }

  /**
   * ID로 특정 채팅 메시지를 조회하는 테스트입니다.
   */
  @Test
  public void testFindChatMessageById() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .content("Find me!")
        .responses(Arrays.asList("Found you!"))
        .timestamp(LocalDateTime.now())
        .build();

    // When: 메시지를 저장한 후, 저장된 메시지의 ID를 사용하여 조회
    StepVerifier.create(chatMessageRepository.save(chatMessage)
            .flatMap(saved -> chatMessageRepository.findById(saved.getId())))
        // Then: 조회된 메시지가 예상과 일치하는지 검증
        .expectNextMatches(found ->
            found.getContent().equals("Find me!") &&
                found.getResponses().get(0).equals("Found you!")
        )
        .verifyComplete();
  }

  /**
   * 채팅 메시지를 업데이트하는 테스트입니다.
   */
  @Test
  public void testUpdateChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .content("Original content")
        .responses(new ArrayList<>(Arrays.asList("Original response")))
        .timestamp(LocalDateTime.now())
        .build();

    // When: 메시지를 저장한 후, 응답을 업데이트하여 다시 저장
    StepVerifier.create(chatMessageRepository.save(chatMessage)
            .flatMap(saved -> {
              saved.appendResponse("Updated response"); // 응답 추가
              return chatMessageRepository.save(saved);
            }))
        // Then: 업데이트된 메시지가 올바르게 저장되었는지 검증
        .expectNextMatches(updated ->
            updated.getResponses().size() == 2 && // 응답이 두 개여야 함
                updated.getResponses().get(1).equals("Updated response")
        )
        .verifyComplete();
  }

  /**
   * 채팅 메시지를 삭제하는 테스트입니다.
   */
  @Test
  public void testDeleteChatMessage() {
    // Given: 저장할 ChatMessage 객체 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .content("Delete me")
        .responses(Arrays.asList("Goodbye!"))
        .timestamp(LocalDateTime.now())
        .build();

    // When: 메시지를 저장한 후 해당 메시지를 삭제
    StepVerifier.create(chatMessageRepository.save(chatMessage)
            .flatMap(saved -> chatMessageRepository.deleteById(saved.getId())
                .then(chatMessageRepository.findById(saved.getId())))) // 삭제 후 조회
        // Then: 삭제된 메시지가 더 이상 존재하지 않음을 검증
        .expectNextCount(0) // 삭제 후 메시지가 없음을 기대
        .verifyComplete();
  }
}