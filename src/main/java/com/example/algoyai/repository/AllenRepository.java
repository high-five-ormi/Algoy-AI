package com.example.algoyai.repository;

import com.example.algoyai.model.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AllenRepository extends MongoRepository<ChatMessage, String> {
}
