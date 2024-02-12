package com.drag.springstudy.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthController {
    @GetMapping
    public ResponseEntity<?> select(){
        System.out.println("/auth 엔드포인트로 들어옴: GET방식");
        return null;
    }
}
