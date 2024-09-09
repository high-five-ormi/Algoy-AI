package com.example.algoyai.repository.solvedac;

import com.example.algoyai.model.entity.solvedac.QuizRecommend;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AllenRepository extends MongoRepository<QuizRecommend, String> {

}
