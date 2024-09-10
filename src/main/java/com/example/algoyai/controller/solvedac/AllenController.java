package com.example.algoyai.controller.solvedac;

import com.example.algoyai.service.solvedac.AllenApiService;
import com.example.algoyai.service.solvedac.AllenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> allen(@RequestParam String algoyusername, String solvedacusername) throws Exception {
        //System.out.printf("controller check");
        try{
            //solvedAC API 호출하여 문제(Json) 받아옴
            //solvedAC에서 받아온 문제들을 String으로 변환
            //앨런에게 질문할 conent 생성
            String content = allenApiService.sovledacCall(solvedacusername);

            String response = allenApiService.callApi(content, client_id);
            //content와 response 저장하는 로직
            //System.out.println(response);
            //allenService.Save(algoyusername, content, response);
            //System.out.println("DB 저장 체크");
        return ResponseEntity.ok(response); // 결과를 보여줄 템플릿 이름
        }catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다"); // 오류를 보여줄 템플릿 이름
        }

    }

    @GetMapping("/response")
    public ResponseEntity<String> fivereponse(@RequestParam String algoyusername, String solvedacusername) throws Exception {
        System.out.println("controller check2");
        try {
            //앨런에게 질문할 conent 생성
            String content = allenApiService.sovledacResponseCall(solvedacusername);

            String response = allenApiService.callApi(content, client_id);
            //System.out.println(content);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다"); // 오류를 보여줄 템플릿 이름
        }

    }
}
