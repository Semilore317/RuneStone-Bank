package com.abraham_bankole.runestone_bank.user.controller;

import com.abraham_bankole.runestone_bank.user.dto.*;
import com.abraham_bankole.runestone_bank.transaction.dto.*;
import com.abraham_bankole.runestone_bank.common.dto.*;
import com.abraham_bankole.runestone_bank.email.dto.*;;
import com.abraham_bankole.runestone_bank.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
  @Autowired private UserService userService;

  @Operation(summary = "Login", description = "Authenticate user and return token")
  @PostMapping("/login")
  public BankResponse login(@RequestBody LoginDto loginDto) {
    return userService.login(loginDto);
  }

  @Operation(
      summary = "Create New User Account",
      description = "Create a user and assign a unique account number")
  @ApiResponse(responseCode = "201", description = "HttpS Status 201 CREATED")
  @PostMapping
  public BankResponse createAccount(@RequestBody UserRequest userRequest) {
    return userService.createAccount(userRequest);
  }

  @Operation(summary = "Balance Enquiry", description = "Get current balance for a user account")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/balanceEnquiry")
  public BankResponse getBalanceEnquiry(@RequestBody EnquiryRequest request) {
    return userService.balanceEnquiry(request);
  }

  @Operation(
      summary = "Name Enquiry",
      description = "Check if a user account exists and get the name")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/nameEnquiry")
  public BankResponse nameEnquiry(@RequestBody EnquiryRequest request) {
    return userService.nameEnquiry(request);
  }
}
