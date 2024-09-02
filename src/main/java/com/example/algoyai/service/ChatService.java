package com.example.algoyai.service;

import com.example.algoyai.model.dto.ChatMessageDto;
import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.repository.ChatMessageRepository;
import com.example.algoyai.util.ChatMessageMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<ChatMessageDto> getChatResponse(String content) {
        return createInitialChatMessage(content)
            .flatMapMany(this::streamResponses);
    }

    private Mono<ChatMessage> createInitialChatMessage(String content) {
        return Mono.just(ChatMessage.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .build())
            .flatMap(chatMessageRepository::save);
    }

    private Flux<ChatMessageDto> streamResponses(ChatMessage initialMessage) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam("content", initialMessage.getContent())
                .queryParam("client_id", apiKey)
                .build())
            .retrieve()
            .bodyToFlux(ChatMessageDto.class)
            .map(response -> {
                ChatMessageMapper.updateEntity(initialMessage, response);
                return response;
            })
            .timeout(Duration.ofSeconds(90))
            .onErrorResume(this::handleError)
            .doOnComplete(() -> saveFinalMessage(initialMessage).subscribe());
    }

    private Flux<ChatMessageDto> handleError(Throwable error) {
        System.err.println("Error occurred: " + error.getMessage());
        return Flux.just(ChatMessageDto.builder()
            .type("error")
            .data(ChatMessageDto.ChatResponseData.builder()
                .content("Error: " + error.getMessage())
                .build())
            .build());
    }

    private Mono<ChatMessage> saveFinalMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
}