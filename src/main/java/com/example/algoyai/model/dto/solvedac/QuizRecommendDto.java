package com.example.algoyai.model.dto.solvedac;

import com.example.algoyai.model.entity.solvedac.QuizRecommend;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuizRecommendDto {
    @Id
    private String id;
    private String userId; //사용자 id
    private String content; //질문 내용
    private String response; // 앨런의 답변 내용
    private LocalDateTime timeStamp; // 저장 시간

    //Entity를 Dto로 변환하는 정적 메소드
    public static QuizRecommendDto fromEntity(QuizRecommend quizRecommend) {
        return QuizRecommendDto.builder()
                .id(quizRecommend.getId())
                .userId(quizRecommend.getUserId())
                .content(quizRecommend.getContent())
                .response(quizRecommend.getResponse())
                .timeStamp(quizRecommend.getTimeStamp())
                .build();
    }

    //DTO를 Entity로 변환하는 메서드
    public QuizRecommend toEntity(){
        return new QuizRecommend(
                this.id,
                this.userId,
                this.content,
                this.response,
                this.timeStamp
        );
    }

}
