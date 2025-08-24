package com.skb.moneymanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0")
public class HomeController {

    @GetMapping("/status")
    public String status() {
        return "Application is running";
    }
}
