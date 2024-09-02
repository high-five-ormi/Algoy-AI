package com.example.algoyai.repository;

import com.example.algoyai.model.entity.ChatMessage;
import com.example.algoyai.model.entity.QuizRecommend;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AllenRepository extends MongoRepository<QuizRecommend, String> {

}
