package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/casebycase")
public class MainController {

    @GetMapping("/session1")
    public String session1() {
        return "content/session1";
    }
}