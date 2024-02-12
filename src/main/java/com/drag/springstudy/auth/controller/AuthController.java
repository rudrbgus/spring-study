package com.drag.springstudy.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AuthController {
    @Value("${google.secret-key}")
    private String GoogleSecretKey;

    @GetMapping
    public ResponseEntity<?> select() {
//        System.out.println("/auth 엔드포인트로 들어옴: GET방식");
        return ResponseEntity.ok().body("안녕ㅋㅋ");
    }

    @GetMapping("/googleLoginApi")
    public void redirectEndPoint(String code, String scope) {
        System.out.println(code);
        System.out.println(scope);
        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate();
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("redirect_uri", "http://localhost:8181/auth/googleLoginApi");
        requestBody.add("client_id", "508435362978-q1dmvpe2to6i1vam4j5bdr25r5sujd2c.apps.googleusercontent.com");
        requestBody.add("client_secret", GoogleSecretKey);

        // HTTP 요청 설정
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(requestBody, headers),
                String.class
        );

        // 응답 확인
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
//            System.out.println("액세스 토큰 요청 성공:\n" + responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            List<String> list = new ArrayList<>();
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                jsonNode.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue().asText();
                    list.add(value);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            RestTemplate restTemplate2 = new RestTemplate();


            HttpHeaders headers2 = new HttpHeaders();
            headers.add("Authorization", "Bearer " + list.get(0));

            // URL에 쿼리 매개변수를 추가하는 방법
            URI uri = UriComponentsBuilder.fromUriString("https://www.googleapis.com/userinfo/v2/me")
                    .queryParam("access_token", list.get(0))
                    .build()
                    .toUri();

            // GET 요청을 수행하고 응답을 받아옴
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
            System.out.println(requestEntity.getBody());
        } else {
            System.err.println("액세스 토큰 요청 실패. 응답 코드: " + responseEntity.getStatusCodeValue());
        }


    }


}
