package com.abraham_bankole.runestone_bank.service.impl;

import com.abraham_bankole.runestone_bank.dto.*;
import com.abraham_bankole.runestone_bank.entity.User;
import com.abraham_bankole.runestone_bank.repository.UserRepository;
import com.abraham_bankole.runestone_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
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

        EmailDetails emailDetails = EmailDetails.builder()
                .recipientEmail(userRequest.getEmail())
                .recipientName(userRequest.getFirstName() + " " + userRequest.getLastName())
                .subject("ACCOUNT CREATION SUCCESSFUL")
                .messageBody("Congratulations! Your account with Runestone Bank has been successfully created! \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" +
                        "Account Number: " + savedUser.getAccountNumber()
                )
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
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
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean doesAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

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
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    @Transactional // for atomicity purpses
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean doesAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User recipient = userRepository.findByAccountNumber(request.getAccountNumber());
        recipient.setAccountBalance(recipient.getAccountBalance().add(request.getAmount()));
        userRepository.save(recipient);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(recipient.getFirstName() + " " + recipient.getLastName() + " " + recipient.getOtherName())
                        .accountNumber(recipient.getAccountNumber())
                        .accountBalance(recipient.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    @Transactional // to make it atomic - it entirely fails or entirely fails
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean doesAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .build();
        }

        User payer = userRepository.findByAccountNumber(request.getAccountNumber());

        BigDecimal newBalance = payer.getAccountBalance().subtract(request.getAmount());

        // FIX: Check if the new balance is LESS THAN ZERO (Insufficient Funds)
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            payer.setAccountBalance(newBalance);
            userRepository.save(payer);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(payer.getAccountNumber())
                            .accountName(payer.getFirstName() + " " + payer.getLastName() + "  " + payer.getOtherName())
                            .accountBalance(payer.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    @Transactional
    public BankResponse transfer(TransferRequest request) {
        // get sender account number
        // confirm that account balance > amount
        // get the account to credit
        // credit the account

        boolean doesReceiverAccountExist = userRepository.existsByAccountNumber(request.getReceiver());

        if(!doesReceiverAccountExist) {
            return  BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sender = userRepository.findByAccountNumber(request.getSender());
        User receiver = userRepository.findByAccountNumber(request.getReceiver());
        String recipientUsername = receiver.getFirstName() + receiver.getLastName() + receiver.getOtherName();
        userRepository.save(receiver);

        // manually log amounts
//
//        System.out.println("Sender Balance: " + sender.getAccountBalance());
//        System.out.println("Request Amount: " + request.getAmount());
        if(request.getAmount().compareTo(sender.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //jss1 business studies nostalgia lmao

        // debit the giver
        sender.setAccountBalance(sender.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sender);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipientName(recipientUsername)
                .recipientEmail(receiver.getEmail())
                .messageBody("You have successfully sent the Sum Of $" + request.getAmount() +" to " +
                        receiver.getFirstName() + receiver.getLastName() + receiver.getOtherName() +
                        " and your account has been debited.")
                //TODO: .attachment() i'll add pdf or image receipts later
                .build();
        emailService.sendEmailAlert(debitAlert);

        // credit the receiver
        receiver.setAccountBalance(receiver.getAccountBalance().add(request.getAmount()));
        userRepository.save(receiver);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipientName(recipientUsername)
                .recipientEmail(receiver.getEmail())
                .messageBody("The Sum Of $" + request.getAmount() + "has been sent to your account from" +
                        sender.getFirstName() + " " + sender.getLastName() + "  " + sender.getOtherName() +
                        "\n Your current balance is " + receiver.getAccountBalance()
                )
                .build();

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSACTION_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSACTION_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }
}