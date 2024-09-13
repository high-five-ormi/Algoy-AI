package com.example.algoyai.service.solvedac;

import com.example.algoyai.model.dto.solvedac.SolvedACResponse;
import com.example.algoyai.repository.solvedac.AllenRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AllenApiService {
    private final HttpURLConnectionEx httpEx;
    private final AllenRepository allenRepository;

    //앨런 api 요청 url
    @Value("${allenApi.url}")
    String basicUrl;

    //solvedAC 요청 url
    @Value("${solvedac.url}")
    String solvedAcApi;

    //api 호출 관련 로직
    @Autowired
    public AllenApiService(AllenRepository allenRepository){
        this.httpEx = new HttpURLConnectionEx();
        this.allenRepository = allenRepository;
    }
    //앨런 api 호출 코드
    public String callApi(String content, String client_id) throws Exception {
        String new_content = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());

        String requestUrl = basicUrl + new_content + "&client_id=" + client_id;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            //API 응답을 받는다 (Json 형태)
            String result = httpEx.get(requestUrl, headers);
            //System.out.println(result);

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

    //userName을 이용하여 sovledAC API를 통해 푼 문제 정보를 호출한다.
    public String sovledacCall(String userName) throws Exception {

        String requestUrl = solvedAcApi + userName;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String content = "현재까지 백준에서 푼 문제번호들을 줄테니 이 문제를 바탕으로 비슷한 수준의 문제를 백준에서 1문제 추천해줘./n "
                + "Json String 형식으로 아래처럼 답변해줘. key는 site, title, problemNo, details야";

        try{
            //API 응답을 받는다(Json 형태)
            String result = httpEx.get(requestUrl, headers);
            //System.out.println(result);

            content += parseSolvedACTitles(result);

            //인코딩 설정해야함
            //String new_content = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            //String response = callApi(new_content, client_id);
            //content와 response 저장하는 로직
            //System.out.println(response);
            //allenService.Save(algoyusername, content, response);
            //System.out.println("DB 저장 체크");


            return content;

        } catch (Exception e){
            throw new Exception("solvedAC 호출 실패", e); //예외 발생시 상위로 전달
        }
    }

    //호출한 정보(solvedAC유저가 푼 문제)를 String 타입으로 변환한다.
    public String parseSolvedACTitles(String jsonResponse){
        //Gson 객체 생성
        Gson gson = new Gson();

        //Json을 SolvedACResponse 객체로 파싱
        SolvedACResponse response = gson.fromJson(jsonResponse, SolvedACResponse.class);

        //결과를 담을 스트링 생성
        String solvedTitles = "";
        //문제 번호를 담을 스트링 생성
        String solvedProblemId = "";
        //items 배열의 각 요소에서 titles의 title 값을 추출
        for (SolvedACResponse.Item item : response.getItems()){
            if(item.getProblemId() != null){
                //System.out.println("probelId: " + item.getProblemId());
                solvedProblemId += item.getProblemId();
                solvedProblemId += ", ";
            }
            //titles 배열의 각 titles을 추가
//            for(SolvedACResponse.Title title : item.getTitles()){
//                if (title.getTitle() != null){
//                    solvedTitles += title.getTitle(); //user가 푼 문제
//                    solvedTitles += ", ";
//                }
//            }
        }
        //System.out.println(solvedProblemId);
        return solvedProblemId;
    }

    //5문제 추천해달라는 요청과 함께 solvedAC 상위 100문제 가져오기(5문제)
    public String sovledacResponseCall(String userName) throws Exception {

        String requestUrl = solvedAcApi + userName;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String content = "현재까지 백준에서 푼 문제번호들을 줄테니 이 문제를 바탕으로 비슷한 수준의 취업을 위한 코딩테스트 문제를 5문제 추천해줘./n "
                + "Json String 형식으로 아래처럼 답변해줘. key는 site, title, problemNo, details야";

        try{
            //API 응답을 받는다(Json 형태)
            String result = httpEx.get(requestUrl, headers);

            content += parseSolvedACTitles(result);


            //인코딩 설정해야함
            //String new_content = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            //String response = callApi(new_content, client_id);
            //content와 response 저장하는 로직
            //System.out.println(response);
            //allenService.Save(algoyusername, content, response);
            //System.out.println("DB 저장 체크");


            return content;

        } catch (Exception e){
            throw new Exception("solvedAC 호출 실패", e); //예외 발생시 상위로 전달
        }
    }
}
