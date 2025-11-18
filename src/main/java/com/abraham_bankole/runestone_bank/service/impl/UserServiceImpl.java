package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.*;
import com.abraham_bankole.runestone_bank.entity.User;
import com.abraham_bankole.runestone_bank.repository.UserRepository;
import com.abraham_bankole.runestone_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // create an account -> saving a new user into the db
        // validate - check if user already exists
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        // send confirmation mail

        EmailDetails emailDetails = EmailDetails.builder()
                .recipientEmail(userRequest.getEmail())
                .recipientName(userRequest.getFirstName() +  " " + userRequest.getLastName())
                .subject("ACCOUNT CREATION SUCCESSFUL")
                .messageBody("Congratulations! Your account with Runestone Bank has been successfully created! \n" +
                        "Account Name: " + savedUser.getFirstName() + " "  + savedUser.getLastName() + "\n" +
                        "Account Number: " + savedUser.getAccountNumber()
                )
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() +  " " + savedUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // check if the account exists in the db
        boolean doesAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!doesAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_CODE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() +  " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean doesAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if(!doesAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

        // Returning a standard BankResponse containing the name in the accountInfo
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        // We can leave balance null or empty for name enquiry security
                        .build())
                .build();
    }

    // TODOS:
    // balance enquiry
    // name enquiry
    // implement credit, debit and transfer
}
