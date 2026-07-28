package com.proxiad.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/success")
    public String success() {
        //probably make it redirect to frontend
        return "You have been redirected to return_url";
    }

    @GetMapping("/salemu-alaykom")
    public String salemuAlaykom() {
        return "Salemu Alaykom";
    }
    @GetMapping("/cancel")
    public String cancel() {
        //probably make it redirect to frontend
        return "You have been redirected to cancel_url";
    }
}
