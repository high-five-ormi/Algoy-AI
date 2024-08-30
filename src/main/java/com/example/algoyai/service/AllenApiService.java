package com.example.algoyai.service;

import com.example.algoyai.repository.AllenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllenApiService {
    private final HttpURLConnectionEx httpEx;
    private final AllenRepository allenRepository;

    //api 호출 관련 로직
    @Autowired
    public AllenApiService(AllenRepository allenRepository){
        this.httpEx = new HttpURLConnectionEx();
        this.allenRepository = allenRepository;
    }

    public String callApi(String content, String client_id) throws Exception {

        String requestUrl = "https://kdt-api-function.azurewebsites.net/api/v1/question?content=" + content + "&client_id=" + client_id;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            String result = httpEx.get(requestUrl, headers);
            //System.out.println(result);
            return result; // API 응답을 그대로 반환
        } catch (Exception e) {
            throw new Exception("API 호출 실패", e); // 예외 발생 시 상위로 전달
        }
    }



}
