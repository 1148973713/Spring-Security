package com.example.authorization.server.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("hello")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String hello(){
        return "hello security";
    }
}
