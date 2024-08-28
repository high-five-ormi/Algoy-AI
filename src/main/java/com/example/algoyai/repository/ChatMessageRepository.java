package com.example.algoyai.repository;

import com.example.algoyai.model.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author JSW
 *
 * ChatMessage 엔티티를 위한 리포지토리 인터페이스입니다.
 * MongoRepository를 확장하여 MongoDB와의 데이터 액세스를 처리합니다.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

}