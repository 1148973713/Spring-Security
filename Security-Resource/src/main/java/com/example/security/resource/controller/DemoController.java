package com.example.security.resource.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/get")
    public Object get(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String bearer = header.substring(header.lastIndexOf("bearer") + 7);
        return (Claims)Jwts.parser().setSigningKey("test_key".getBytes(StandardCharsets.UTF_8)).parse(bearer).getBody();

        //return "hello world";
    }
}
