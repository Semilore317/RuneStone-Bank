package com.abraham_bankole.runestone_bank.security.service.impl;

import com.abraham_bankole.runestone_bank.common.event.UserLoginEvent;
import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.kafka.KafkaTopics;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.user.dto.LoginDto;
import com.abraham_bankole.runestone_bank.common.dto.AccountInfo;
import com.abraham_bankole.runestone_bank.security.config.JwtTokenProvider;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            OutboxService outboxService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
    }

    public BankResponse login(LoginDto loginDto) {
        // use spring security for authN
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // fetch user details for the response payload
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // publish the domain event
        outboxService.exportEvent(
                user.getAccountNumber(),
                KafkaTopics.USER_LOGIN,
                "userLogin",
                new UserLoginEvent(
                        user.getFirstName(),
                        user.getEmail()
                )
        );

        // return the JWT and Account Info
        return BankResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(AccountUtils.LOGIN_SUCCESS_MESSAGE)
                .jwt(jwtTokenProvider.generateToken(authentication))
                .accountInfo(AccountInfo.builder()
                        .accountName(user.getFullName())
                        .accountNumber(user.getAccountNumber())
                        .accountBalance(user.getAccountBalance())
                        .build())
                .build();
    }
}