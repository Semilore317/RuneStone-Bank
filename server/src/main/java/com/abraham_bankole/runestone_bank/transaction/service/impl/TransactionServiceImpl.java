package com.abraham_bankole.runestone_bank.transaction.service.impl;

import com.abraham_bankole.runestone_bank.common.event.TransactionCompletedEvent;
import com.abraham_bankole.runestone_bank.common.service.UserAccountService;
import com.abraham_bankole.runestone_bank.common.dto.AccountInfo;
import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.dto.TransactionDto;
import com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.transaction.repository.TransactionRepository;
import com.abraham_bankole.runestone_bank.transaction.service.TransactionService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired TransactionRepository transactionRepository;

  @Autowired UserAccountService userAccountService;

  @Autowired ApplicationEventPublisher eventPublisher;

  @Override
  public void saveTransaction(TransactionDto transactionDto) {
    Transaction transaction =
        Transaction.builder()
            .transactionType(transactionDto.getTransactionType())
            .accountNumber(transactionDto.getAccountNumber())
            .amount(transactionDto.getAmount())
            .status("SUCCESS")
            .build();
    transactionRepository.save(transaction);
    System.out.println("Transaction Saved Successfully!");
  }

  @Override
  @Transactional
  public BankResponse creditAccount(CreditDebitRequest request) {
    if (!userAccountService.accountExists(request.getAccountNumber())) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    BigDecimal currentBalance = userAccountService.getBalance(request.getAccountNumber());
    BigDecimal newBalance = currentBalance.add(request.getAmount());
    userAccountService.updateBalance(request.getAccountNumber(), newBalance);

    // save each credit transaction
    TransactionDto transactionDto =
        TransactionDto.builder()
            .accountNumber(request.getAccountNumber())
            .transactionType("CREDIT")
            .amount(request.getAmount())
            .build();
    saveTransaction(transactionDto);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
        .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
        .accountInfo(
            AccountInfo.builder()
                .accountName(userAccountService.getFullName(request.getAccountNumber()))
                .accountNumber(request.getAccountNumber())
                .accountBalance(newBalance)
                .build())
        .build();
  }

  @Override
  @Transactional
  public BankResponse debitAccount(CreditDebitRequest request) {
    if (!userAccountService.accountExists(request.getAccountNumber())) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .build();
    }

    BigDecimal currentBalance = userAccountService.getBalance(request.getAccountNumber());
    BigDecimal newBalance = currentBalance.subtract(request.getAmount());

    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }

    userAccountService.updateBalance(request.getAccountNumber(), newBalance);

    // save each debit transaction
    TransactionDto transactionDto =
        TransactionDto.builder()
            .accountNumber(request.getAccountNumber())
            .transactionType("DEBIT")
            .amount(request.getAmount())
            .build();
    saveTransaction(transactionDto);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
        .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
        .accountInfo(
            AccountInfo.builder()
                .accountNumber(request.getAccountNumber())
                .accountName(userAccountService.getFullName(request.getAccountNumber()))
                .accountBalance(newBalance)
                .build())
        .build();
  }

  @Override
  @Transactional
  public BankResponse transfer(TransferRequest request) {
    if (!userAccountService.accountExists(request.getReceiver())) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    BigDecimal senderBalance = userAccountService.getBalance(request.getSender());

    if (request.getAmount().compareTo(senderBalance) > 0) {
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }

    // debit the sender
    userAccountService.updateBalance(
        request.getSender(), senderBalance.subtract(request.getAmount()));

    // credit the receiver
    BigDecimal receiverBalance = userAccountService.getBalance(request.getReceiver());
    userAccountService.updateBalance(
        request.getReceiver(), receiverBalance.add(request.getAmount()));

    // publish domain event — the email domain listens and sends debit/credit alerts
    String senderName = userAccountService.getFullName(request.getSender());
    String senderEmail = userAccountService.getEmail(request.getSender());
    String receiverName = userAccountService.getFullName(request.getReceiver());
    String receiverEmail = userAccountService.getEmail(request.getReceiver());
    eventPublisher.publishEvent(new TransactionCompletedEvent(
        request.getSender(), senderName, senderEmail,
        request.getReceiver(), receiverName, receiverEmail,
        request.getAmount(), "TRANSFER"
    ));

    // log debit transaction for sender
    TransactionDto debitTransaction =
        TransactionDto.builder()
            .accountNumber(request.getSender())
            .transactionType("DEBIT")
            .amount(request.getAmount())
            .build();
    saveTransaction(debitTransaction);

    // log credit transaction for receiver
    TransactionDto creditTransaction =
        TransactionDto.builder()
            .accountNumber(request.getReceiver())
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

  @Override
  public java.util.List<Transaction> getTransactionHistory(String accountNumber) {
    return transactionRepository.findByAccountNumberOrderByTimeOfCreationDesc(accountNumber);
  }
}
