package com.example.algoyai.service.solvedac;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpURLConnectionEx {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String get(String requestUrl, Map<String, String> headers) {
        try {
            URL obj = new URL(requestUrl);
            HttpURLConnection con = requestUrl.startsWith("https") ? (HttpsURLConnection) obj.openConnection() : (HttpURLConnection) obj.openConnection();
            //HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            //con.setRequestProperty("charset", "utf-8");
//            for (Map.Entry<String, String> header : headers.entrySet()) {
//                con.setRequestProperty(header.getKey(), header.getValue());
//            }

            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            con.setRequestMethod("GET");
            con.setDoOutput(false);
            con.connect();

            int resCode = con.getResponseCode();

            if (resCode != HttpURLConnection.HTTP_OK) {
                System.out.printf("연결안됨");
                con.disconnect();
                throw new MyHttpFailRuntimeException("HTTP response code: " + resCode);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            //System.out.println(response.toString());
            br.close();
            con.disconnect();
            return response.toString();
        } catch (IOException e) {
            System.out.println("IO Exception: " + e);
            throw new MyException("IO Exception occurred", e);
        } catch (MyHttpFailRuntimeException e) {
            System.out.println("HTTP Fail: " + e);
            throw e;
        }
    }
}

class MyHttpFailRuntimeException extends RuntimeException {
    public MyHttpFailRuntimeException(String message) {
        super(message);
    }
}

class MyException extends RuntimeException {
    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}
