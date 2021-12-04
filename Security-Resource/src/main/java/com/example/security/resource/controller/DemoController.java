package com.example.security.resource.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/get")
    public Object get() {
        return "获取成功";
    }
}
