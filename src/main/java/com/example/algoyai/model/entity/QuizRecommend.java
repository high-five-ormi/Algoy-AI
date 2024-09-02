package com.example.algoyai.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "quiz_recommend")
public class QuizRecommend {
    @Id
    private String id;

    private String userId; //사용자 id
    private String content; //질문 내용
    private String response; // 앨런의 답변 내용
    private LocalDateTime timeStamp; // 저장 시간


}
