package com.abraham_bankole.runestone_bank.controller;

import com.abraham_bankole.runestone_bank.dto.*;
import com.abraham_bankole.runestone_bank.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Create a user and assign a unique account number"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HttpS Status 201 CREATED"
    )
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
    public BankResponse credit(@RequestBody CreditDebitRequest creditRequest) {
        return userService.creditAccount(creditRequest);
    }

    @PostMapping("/debit")
    public BankResponse debit(@RequestBody CreditDebitRequest creditRequest) {
        return userService.debitAccount(creditRequest);
    }

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest) {
        return userService.transfer(transferRequest);
    }
}