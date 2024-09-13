package com.example.algoyai.service.solvedac;

import com.example.algoyai.model.dto.solvedac.QuizRecommendDto;
import com.example.algoyai.model.entity.solvedac.QuizRecommend;
import com.example.algoyai.repository.solvedac.AllenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AllenService { // 데이터를 저장하거나 조회하는 메서드

    private final AllenRepository allenRepository;

    @Autowired
    public AllenService(AllenRepository allenRepository) {
        this.allenRepository = allenRepository;
    }

//데이터 저장하는 메서드
    public ResponseEntity<String> Save(String userId, String content, String response) {
        //System.out.println("dto check");
        QuizRecommendDto quizRecommendDto = QuizRecommendDto.builder()
                        .userId(userId)
                        .content(content)
                        .response(response)
                        .timeStamp(LocalDateTime.now())
                        .build();
        //System.out.println("dto check");

        //dto to entity
        QuizRecommend quizRecommend = quizRecommendDto.toEntity();
        //System.out.println("entity check");
        //System.out.println(quizRecommend.getId());
        //System.out.println(quizRecommend.getResponse());

        //MongoDB에 저장
        allenRepository.save(quizRecommend);
        //System.out.println("save success");
        return ResponseEntity.ok("Data saved successfully!");
    }


}
