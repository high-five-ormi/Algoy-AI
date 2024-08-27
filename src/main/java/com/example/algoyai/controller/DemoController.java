package com.example.algoyai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class DemoController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String test(){
        return "test";
    }
}