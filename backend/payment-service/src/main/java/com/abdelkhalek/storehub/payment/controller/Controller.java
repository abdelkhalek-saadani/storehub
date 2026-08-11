package com.abdelkhalek.storehub.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Controller {

    @GetMapping()
    public String salemuAlaykom() {
        return "Salemu Alaykom";
    }
}
