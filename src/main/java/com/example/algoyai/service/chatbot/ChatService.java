package com.example.algoyai.service.chatbot;

import com.example.algoyai.model.dto.chatbot.ChatMessageDto;
import com.example.algoyai.model.entity.chatbot.ChatMessage;
import com.example.algoyai.repository.chatbot.ChatMessageRepository;
import com.example.algoyai.util.chatbot.ChatMessageMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author JSW
 *
 * ChatService 클래스는 AI 챗봇과의 상호작용을 관리하는 서비스 계층입니다.
 * 이 클래스는 웹 클라이언트를 통해 외부 API와 통신하고, MongoDB에 채팅 메시지를 저장합니다.
 */
@Service
public class ChatService {

    private final WebClient webClient;
    private final ChatMessageRepository chatMessageRepository;

    @Value("${ai.api.key}")
    private String apiKey;

    /**
     * ChatService 생성자.
     *
     * @param webClientBuilder WebClient를 생성하기 위한 빌더 객체.
     * @param apiUrl           외부 AI API의 기본 URL.
     * @param chatMessageRepository MongoDB에 저장된 ChatMessage를 관리하는 리포지토리.
     */
    public ChatService(WebClient.Builder webClientBuilder,
        @Value("${ai.api.url}") String apiUrl,
        ChatMessageRepository chatMessageRepository) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * 사용자가 입력한 메시지에 대한 AI의 응답을 가져옵니다.
     *
     * @param content 사용자가 입력한 메시지 내용.
     * @return AI의 응답을 포함하는 ChatMessageDto 객체의 Flux 스트림.
     */
    public Flux<ChatMessageDto> getChatResponse(String content) {
        return createInitialChatMessage(content)
            .flatMapMany(this::streamResponses);
    }

    /**
     * 초기 채팅 메시지를 생성하고 이를 MongoDB에 저장합니다.
     *
     * @param content 사용자가 입력한 메시지 내용.
     * @return 생성된 ChatMessage 객체를 담고 있는 Mono.
     */
    private Mono<ChatMessage> createInitialChatMessage(String content) {
        return Mono.just(ChatMessage.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .build())
            .flatMap(chatMessageRepository::save);
    }

    /**
     * AI API로부터 스트리밍 방식으로 응답을 받아 처리하고, 최종적으로 MongoDB에 저장합니다.
     *
     * @param initialMessage 초기 채팅 메시지 객체.
     * @return AI의 응답을 포함하는 ChatMessageDto 객체의 Flux 스트림.
     */
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

    /**
     * API 호출 중 오류가 발생했을 때 이를 처리하고, 오류 메시지를 반환합니다.
     *
     * @param error 발생한 오류.
     * @return 오류 메시지를 포함하는 ChatMessageDto 객체의 Flux 스트림.
     */
    private Flux<ChatMessageDto> handleError(Throwable error) {
        System.err.println("Error occurred: " + error.getMessage());
        return Flux.just(ChatMessageDto.builder()
            .type("error")
            .data(ChatMessageDto.ChatResponseData.builder()
                .content("Error: " + error.getMessage())
                .build())
            .build());
    }

    /**
     * 최종적으로 MongoDB에 채팅 메시지를 저장합니다.
     *
     * @param chatMessage 저장할 ChatMessage 객체.
     * @return 저장된 ChatMessage 객체를 담고 있는 Mono.
     */
    private Mono<ChatMessage> saveFinalMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
}