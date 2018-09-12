package com.balceda.itlict.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @RequestMapping("/")
    public String index(){
        String response = "Welcome to ITLICT REST API for IBM DataCap";
        return response;
    }
}
