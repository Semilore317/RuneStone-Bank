package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.AccountInfo;
import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.event.UserRegisteredEvent;
import com.abraham_bankole.runestone_bank.common.kafka.KafkaTopics;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.security.entity.Role;
import com.abraham_bankole.runestone_bank.user.dto.*;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import com.abraham_bankole.runestone_bank.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    public final OutboxService outboxService;

    public UserServiceImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OutboxService outboxService
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
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
        outboxService.exportEvent(
                savedUser.getAccountNumber(),
                KafkaTopics.USER_REGISTERED,
                "UserRegistered",
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getFirstName())
        );

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountNumber(savedUser.getAccountNumber())
                                .accountBalance(savedUser.getAccountBalance())
                                .accountName(savedUser.getFullName())
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
                                .accountName(foundUser.getFullName())
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
                                .accountName(foundUser.getFullName())
                                .build())
                .build();
    }

    @Override
    public BankResponse getProfile(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber);
        if (user == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .build();
        }

        UserProfileResponse profileData = UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .accountNumber(user.getAccountNumber())
                .emailNotifs(user.getEmailNotifs())
                .loginAlerts(user.getLoginAlerts())
                .transferAlerts(user.getTransferAlerts())
                .build();

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .data(profileData)
                .build();
    }

    @Override
    public BankResponse updateProfile(String accountNumber, ProfileUpdateRequest request) {
        User user = userRepository.findByAccountNumber(accountNumber);
        if (user == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .build();
        }

        user.setFirstName(request.getFirstName() != null ? request.getFirstName() : user.getFirstName());
        user.setLastName(request.getLastName() != null ? request.getLastName() : user.getLastName());
        user.setEmail(request.getEmail() != null ? request.getEmail() : user.getEmail());
        user.setEmailNotifs(request.getEmailNotifs() != null ? request.getEmailNotifs() : user.getEmailNotifs());
        user.setLoginAlerts(request.getLoginAlerts() != null ? request.getLoginAlerts() : user.getLoginAlerts());
        user.setTransferAlerts(request.getTransferAlerts() != null ? request.getTransferAlerts() : user.getTransferAlerts());

        userRepository.save(user);

        return BankResponse.builder()
                .responseCode(AccountUtils.PROFILE_UPDATE_SUCCESS_CODE)
                .responseMessage(AccountUtils.PROFILE_UPDATE_SUCCESS_MESSAGE)
                .build();
    }

    @Override
    public BankResponse updatePassword(String accountNumber, PasswordUpdateRequest request) {
        User user = userRepository.findByAccountNumber(accountNumber);
        if (user == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .build();
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.PASSWORD_INCORRECT_CODE)
                    .responseMessage(AccountUtils.PASSWORD_INCORRECT_MESSAGE)
                    .build();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return BankResponse.builder()
                .responseCode(AccountUtils.PASSWORD_UPDATE_SUCCESS_CODE)
                .responseMessage(AccountUtils.PASSWORD_UPDATE_SUCCESS_MESSAGE)
                .build();
    }

}
