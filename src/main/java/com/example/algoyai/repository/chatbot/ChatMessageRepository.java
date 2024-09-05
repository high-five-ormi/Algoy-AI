package com.example.algoyai.repository.chatbot;

import com.example.algoyai.model.entity.chatbot.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JSW
 *
 * ChatMessageRepository는 MongoDB와 상호작용하는 리액티브 리포지토리 인터페이스입니다.
 * 이 인터페이스는 ReactiveMongoRepository를 상속받아 비동기 방식으로 데이터를 처리할 수 있습니다.
 */
@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

}