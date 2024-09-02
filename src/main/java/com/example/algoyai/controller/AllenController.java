package com.example.algoyai.controller;

import com.example.algoyai.service.AllenApiService;
import com.example.algoyai.service.AllenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/ai/allenapi")
public class AllenController {

    //앨런 api 키 값
    @Value("${allenApi.key}")
    String client_id;

    private AllenApiService allenApiService;
    private AllenService allenService;

    @Autowired
    public AllenController(AllenApiService allenApiService, AllenService allenService) {
        this.allenApiService = allenApiService;
        this.allenService = allenService;
    }

    @GetMapping
    public ResponseEntity<String> allen(@RequestParam String userId, String content) throws Exception {
        System.out.printf("controller check");
        try{
            //인코딩 설정해야함
            String new_content = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            String response = allenApiService.callApi(new_content, client_id);
            //content와 response 저장하는 로직
            //System.out.println(response);
            allenService.Save(userId, content, response);
            //System.out.println("DB 저장 체크");
        return ResponseEntity.ok(response); // 결과를 보여줄 템플릿 이름
        }catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다"); // 오류를 보여줄 템플릿 이름
        }

    }
}
