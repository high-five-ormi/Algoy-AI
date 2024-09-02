package com.example.algoyai.service;

import com.example.algoyai.repository.AllenRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllenApiService {
    private final HttpURLConnectionEx httpEx;
    private final AllenRepository allenRepository;

    @Value("${AllenApi.url}")
    String basicUrl;

    //api 호출 관련 로직
    @Autowired
    public AllenApiService(AllenRepository allenRepository){
        this.httpEx = new HttpURLConnectionEx();
        this.allenRepository = allenRepository;
    }

    public String callApi(String content, String client_id) throws Exception {

        String requestUrl = basicUrl + content + "&client_id=" + client_id;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            //API 응답을 받는다 (Json 형태)
            String result = httpEx.get(requestUrl, headers);
            System.out.println(result);

            //Json을 파싱하여 content만 반환한다.
            //Gson라이브러리 : Google에서 제공하는 JSON 파싱 라이브러리
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(result, JsonObject.class);
            String responseContent = jsonResponse.get("content").getAsString();

            return responseContent; // content 필드만 반환
        } catch (Exception e) {
            throw new Exception("API 호출 실패", e); // 예외 발생 시 상위로 전달
        }
    }



}
