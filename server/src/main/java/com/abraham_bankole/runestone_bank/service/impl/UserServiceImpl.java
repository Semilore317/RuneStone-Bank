package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.config.JwtTokenProvider;
import com.abraham_bankole.runestone_bank.dto.*;
import com.abraham_bankole.runestone_bank.entity.Role;
import com.abraham_bankole.runestone_bank.entity.User;
import com.abraham_bankole.runestone_bank.repository.UserRepository;
import com.abraham_bankole.runestone_bank.service.EmailService;
import com.abraham_bankole.runestone_bank.service.UserService;
import com.abraham_bankole.runestone_bank.utils.AccountUtils;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private EmailService emailService;

  @Autowired public PasswordEncoder passwordEncoder;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Override
  public BankResponse createAccount(UserRequest userRequest) {
    if (userRepository.existsByEmail(userRequest.getEmail())) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
          .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User newUser =
        User.builder()
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .otherName(userRequest.getOtherName())
            .gender(userRequest.getGender())
            .address(userRequest.getAddress())
            .stateOfOrigin(userRequest.getStateOfOrigin())
            .accountNumber(AccountUtils.generateAccountNumber())
            .accountBalance(BigDecimal.ZERO)
            .email(userRequest.getEmail())
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .phoneNumber(userRequest.getPhoneNumber())
            .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
            .role(Role.valueOf("ROLE_ADMIN"))
            .status("ACTIVE")
            .build();

    User savedUser = userRepository.save(newUser);

    EmailDetails emailDetails =
        EmailDetails.builder()
            .recipientEmail(userRequest.getEmail())
            .recipientName(userRequest.getFirstName() + " " + userRequest.getLastName())
            .subject("ACCOUNT CREATION SUCCESSFUL")
            .messageBody(
                "Congratulations! Your account with Runestone Bank has been successfully created! \n"
                    + "Account Name: "
                    + savedUser.getFirstName()
                    + " "
                    + savedUser.getLastName()
                    + "\n"
                    + "Account Number: "
                    + savedUser.getAccountNumber())
            .build();
    emailService.sendEmailAlert(emailDetails);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
        .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
        .accountInfo(
            AccountInfo.builder()
                .accountNumber(savedUser.getAccountNumber())
                .accountBalance(savedUser.getAccountBalance())
                .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .build())
        .build();
  }

  public BankResponse login(LoginDto loginDto) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

    EmailDetails loginAlert =
        EmailDetails.builder()
            .subject("Runestone Bank Login Alert")
            .messageBody("You logged into your account")
            .build();

    emailService.sendEmailAlert(loginAlert);

    User user =
        userRepository
            .findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
    return BankResponse.builder()
        .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
        .responseMessage(AccountUtils.LOGIN_SUCCESS_MESSAGE)
        .jwt(jwtTokenProvider.generateToken(authentication))
        .accountInfo(
            AccountInfo.builder()
                .accountName(
                    user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .build())
        .build();
  }

  @Override
  public BankResponse balanceEnquiry(EnquiryRequest request) {
    boolean doesAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

    if (!doesAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
        .accountInfo(
            AccountInfo.builder()
                .accountBalance(foundUser.getAccountBalance())
                .accountNumber(foundUser.getAccountNumber())
                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                .build())
        .build();
  }

  @Override
  public BankResponse nameEnquiry(EnquiryRequest enquiryRequest) {
    boolean doesAccountExists =
        userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

    if (!doesAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
        .accountInfo(
            AccountInfo.builder()
                .accountName(
                    foundUser.getFirstName()
                        + " "
                        + foundUser.getLastName()
                        + " "
                        + foundUser.getOtherName())
                .accountNumber(foundUser.getAccountNumber())
                .build())
        .build();
  }

  public static void main(String[] args) {
    UserServiceImpl userService = new UserServiceImpl();
    System.out.println(userService.passwordEncoder.encode("clown_password🤡"));
  }
}
