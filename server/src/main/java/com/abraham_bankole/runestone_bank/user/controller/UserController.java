package com.abraham_bankole.runestone_bank.user.controller;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.user.dto.EnquiryRequest;
import com.abraham_bankole.runestone_bank.user.dto.LoginDto;
import com.abraham_bankole.runestone_bank.user.dto.UserRequest;
import com.abraham_bankole.runestone_bank.user.dto.ProfileUpdateRequest;
import com.abraham_bankole.runestone_bank.user.dto.PasswordUpdateRequest;
import com.abraham_bankole.runestone_bank.user.service.UserService;
import com.abraham_bankole.runestone_bank.security.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Account Management APIs")
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private AuthServiceImpl authService;

  @Operation(summary = "Login", description = "Authenticate user and return token")
  @PostMapping("/login")
  public BankResponse login(@RequestBody LoginDto loginDto) {
    return authService.login(loginDto);
  }

  @Operation(summary = "Logout", description = "Invalidate user token")
    @PostMapping("/logout")
    public BankResponse logout(@RequestHeader("Authorization") String authHeader) {
        return authService.logout(authHeader);
    }

  @Operation(summary = "Create New User Account", description = "Create a user and assign a unique account number")
  @ApiResponse(responseCode = "201", description = "HttpS Status 201 CREATED")
  @PostMapping
  public BankResponse createAccount(@RequestBody UserRequest userRequest) {
    return userService.createAccount(userRequest);
  }

  @Operation(summary = "Balance Enquiry", description = "Get current balance for a user account")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/balanceEnquiry")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public BankResponse getBalanceEnquiry(@RequestParam String accountNumber) {
    return userService.balanceEnquiry(new EnquiryRequest(accountNumber));
  }

  @Operation(summary = "Name Enquiry", description = "Check if a user account exists and get the name")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/nameEnquiry")
  @PreAuthorize("isAuthenticated()")
  public BankResponse nameEnquiry(@RequestParam String accountNumber) {
    return userService.nameEnquiry(new EnquiryRequest(accountNumber));
  }

  @Operation(summary = "Get User Profile", description = "Get current user profile settings")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/profile")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public BankResponse getProfile(@RequestParam String accountNumber) {
    return userService.getProfile(accountNumber);
  }

  @Operation(summary = "Update User Profile", description = "Update user profile settings")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @PutMapping("/profile")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public BankResponse updateProfile(@RequestParam String accountNumber, @RequestBody ProfileUpdateRequest request) {
    return userService.updateProfile(accountNumber, request);
  }

  @Operation(summary = "Update Password", description = "Update user password")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @PutMapping("/password")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public BankResponse updatePassword(@RequestParam String accountNumber, @RequestBody PasswordUpdateRequest request) {
    return userService.updatePassword(accountNumber, request);
  }
}