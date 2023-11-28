package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartPageController {

    @GetMapping("/")
    public String showHomePage(Model model) {
        return "index";
    }
    
    @GetMapping("/layout")
    public String showHomePageLayout(Model model) {
        return "layout";
    }
}