package com.example.algoyai.repository.chatbot;

import com.example.algoyai.model.entity.chatbot.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

@DataMongoTest
public class ChatMessageRepositoryTest {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@BeforeEach
	public void setUp() {
		chatMessageRepository.deleteAll().block();
	}

	@Test
	public void testSaveChatMessage() {
		ChatMessage chatMessage = ChatMessage.builder()
			.content("Hello, AI!")
			.responses(Arrays.asList("Hello, Human!"))
			.timestamp(LocalDateTime.now())
			.build();

		Mono<ChatMessage> savedMessage = chatMessageRepository.save(chatMessage);

		StepVerifier.create(savedMessage)
			.expectNextMatches(saved ->
				saved.getId() != null &&
					saved.getContent().equals("Hello, AI!") &&
					saved.getResponses().get(0).equals("Hello, Human!")
			)
			.verifyComplete();
	}

	@Test
	public void testFindAllChatMessages() {
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

		chatMessageRepository.saveAll(Arrays.asList(message1, message2)).blockLast();

		Flux<ChatMessage> allMessages = chatMessageRepository.findAll();

		StepVerifier.create(allMessages)
			.expectNextCount(2)
			.verifyComplete();
	}

	@Test
	public void testFindChatMessageById() {
		ChatMessage chatMessage = ChatMessage.builder()
			.content("Find me!")
			.responses(Arrays.asList("Found you!"))
			.timestamp(LocalDateTime.now())
			.build();

		String id = chatMessageRepository.save(chatMessage).block().getId();

		Mono<ChatMessage> foundMessage = chatMessageRepository.findById(id);

		StepVerifier.create(foundMessage)
			.expectNextMatches(found ->
				found.getId().equals(id) &&
					found.getContent().equals("Find me!")
			)
			.verifyComplete();
	}

	@Test
	public void testUpdateChatMessage() {
		ChatMessage chatMessage = ChatMessage.builder()
			.content("Original content")
			.responses(Arrays.asList("Original response"))
			.timestamp(LocalDateTime.now())
			.build();

		String id = chatMessageRepository.save(chatMessage).block().getId();

		Mono<ChatMessage> updatedMessage = chatMessageRepository.findById(id)
			.map(message -> {
				message.appendResponse("Updated response");
				return message;
			})
			.flatMap(chatMessageRepository::save);

		StepVerifier.create(updatedMessage)
			.expectNextMatches(updated ->
				updated.getId().equals(id) &&
					updated.getResponses().size() == 2 &&
					updated.getResponses().get(1).equals("Updated response")
			)
			.verifyComplete();
	}

	@Test
	public void testDeleteChatMessage() {
		ChatMessage chatMessage = ChatMessage.builder()
			.content("Delete me")
			.responses(Arrays.asList("Goodbye!"))
			.timestamp(LocalDateTime.now())
			.build();

		String id = chatMessageRepository.save(chatMessage).block().getId();

		Mono<Void> deleteResult = chatMessageRepository.deleteById(id);

		StepVerifier.create(deleteResult)
			.verifyComplete();

		Mono<ChatMessage> findResult = chatMessageRepository.findById(id);

		StepVerifier.create(findResult)
			.expectNextCount(0)
			.verifyComplete();
	}
}