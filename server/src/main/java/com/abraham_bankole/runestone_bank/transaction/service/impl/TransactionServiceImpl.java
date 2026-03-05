package com.abraham_bankole.runestone_bank.transaction.service.impl;

import com.abraham_bankole.runestone_bank.user.dto.AccountInfo;
import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.email.dto.EmailDetails;
import com.abraham_bankole.runestone_bank.transaction.dto.TransactionDto;
import com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.transaction.repository.TransactionRepository;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import com.abraham_bankole.runestone_bank.email.service.EmailService;
import com.abraham_bankole.runestone_bank.transaction.service.TransactionService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired TransactionRepository transactionRepository;

  @Autowired UserRepository userRepository;

  @Autowired EmailService emailService;

  @Override
  public void saveTransaction(TransactionDto transactionDto) {
    Transaction transaction =
        Transaction.builder()
            .transactionType(transactionDto.getTransactionType())
            .accountNumber(transactionDto.getAccountNumber())
            .amount(transactionDto.getAmount())
            .status("SUCCESS") // not really sure what scenarios would warrant anything other than
            // this...
            .build();
    transactionRepository.save(transaction);
    System.out.println("Transaction Saved Successfully!");
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

    // save each credit transaction
    TransactionDto transactionDto =
        TransactionDto.builder()
            .accountNumber(recipient.getAccountNumber())
            .transactionType("CREDIT")
            .amount(request.getAmount())
            .build();

    saveTransaction(transactionDto);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
        .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
        .accountInfo(
            AccountInfo.builder()
                .accountName(
                    recipient.getFirstName()
                        + " "
                        + recipient.getLastName()
                        + " "
                        + recipient.getOtherName())
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

      // save each dedit transaction
      TransactionDto transactionDto =
          TransactionDto.builder()
              .accountNumber(payer.getAccountNumber())
              .transactionType("DEBIT")
              .amount(request.getAmount())
              .build();

      saveTransaction(transactionDto);
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
          .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
          .accountInfo(
              AccountInfo.builder()
                  .accountNumber(payer.getAccountNumber())
                  .accountName(
                      payer.getFirstName()
                          + " "
                          + payer.getLastName()
                          + "  "
                          + payer.getOtherName())
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

    if (!doesReceiverAccountExist) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User sender = userRepository.findByAccountNumber(request.getSender());
    User receiver = userRepository.findByAccountNumber(request.getReceiver());
    String recipientUsername =
        receiver.getFirstName() + receiver.getLastName() + receiver.getOtherName();
    userRepository.save(receiver);

    // manually log amounts
    //
    // System.out.println("Sender Balance: " + sender.getAccountBalance());
    // System.out.println("Request Amount: " + request.getAmount());
    if (request.getAmount().compareTo(sender.getAccountBalance()) > 0) {
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }

    // jss1 business studies nostalgia lmao

    // debit the giver
    sender.setAccountBalance(sender.getAccountBalance().subtract(request.getAmount()));
    userRepository.save(sender);
    EmailDetails debitAlert =
        EmailDetails.builder()
            .subject("DEBIT ALERT")
            .recipientName(recipientUsername)
            .recipientEmail(receiver.getEmail())
            .messageBody(
                "You have successfully sent the Sum Of $"
                    + request.getAmount()
                    + " to "
                    + receiver.getFirstName()
                    + receiver.getLastName()
                    + receiver.getOtherName()
                    + " and your account has been debited.")
            .build();
    emailService.sendEmailAlert(debitAlert);

    // credit the receiver
    receiver.setAccountBalance(receiver.getAccountBalance().add(request.getAmount()));
    userRepository.save(receiver);
    EmailDetails creditAlert =
        EmailDetails.builder()
            .subject("CREDIT ALERT")
            .recipientName(recipientUsername)
            .recipientEmail(receiver.getEmail())
            .messageBody(
                "The Sum Of $"
                    + request.getAmount()
                    + "has been sent to your account from"
                    + sender.getFirstName()
                    + " "
                    + sender.getLastName()
                    + "  "
                    + sender.getOtherName()
                    + "\n Your current balance is "
                    + receiver.getAccountBalance())
            .build();
    emailService.sendEmailAlert(creditAlert);

    // log debit transaction for sender
    TransactionDto debitTransaction =
        TransactionDto.builder()
            .accountNumber(sender.getAccountNumber())
            .transactionType("DEBIT")
            .amount(request.getAmount())
            .build();
    saveTransaction(debitTransaction);

    // log credit transaction for receiver
    TransactionDto creditTransaction =
        TransactionDto.builder()
            .accountNumber(receiver.getAccountNumber())
            .transactionType("CREDIT")
            .amount(request.getAmount())
            .build();
    saveTransaction(creditTransaction);

    return BankResponse.builder()
        .responseCode(AccountUtils.TRANSACTION_SUCCESSFUL_CODE)
        .responseMessage(AccountUtils.TRANSACTION_SUCCESSFUL_MESSAGE)
        .accountInfo(null)
        .build();
  }
}
