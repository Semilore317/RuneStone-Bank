package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.AccountInfo;
import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.event.UserRegisteredEvent;
import com.abraham_bankole.runestone_bank.security.entity.Role;
import com.abraham_bankole.runestone_bank.user.dto.*;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import com.abraham_bankole.runestone_bank.user.service.UserService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

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

        // publish domain event — the email domain listens and sends the welcome email
        applicationEventPublisher.publishEvent(
                new UserRegisteredEvent(savedUser.getId(), savedUser.getEmail(), savedUser.getFirstName()));

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

}
