package com.nish.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/get")
    public String get(){
        return "hello";
    }
}
