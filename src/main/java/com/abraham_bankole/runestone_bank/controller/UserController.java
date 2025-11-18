package com.abraham_bankole.runestone_bank.controller;

import com.abraham_bankole.runestone_bank.dto.BankResponse;
import com.abraham_bankole.runestone_bank.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.dto.EnquiryRequest;
import com.abraham_bankole.runestone_bank.dto.UserRequest;
import com.abraham_bankole.runestone_bank.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceEnquiry")
    public BankResponse getBalanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    public BankResponse nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public BankResponse credt(@RequestBody CreditDebitRequest creditRequest) {
        return userService.creditAccount(creditRequest);
    }
}