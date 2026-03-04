package com.abraham_bankole.runestone_bank.service;

import com.abraham_bankole.runestone_bank.dto.BankResponse;
import com.abraham_bankole.runestone_bank.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.dto.TransactionDto;
import com.abraham_bankole.runestone_bank.dto.TransferRequest;

public interface TransactionService {
  void saveTransaction(TransactionDto transaction);

  BankResponse creditAccount(CreditDebitRequest request);

  BankResponse debitAccount(CreditDebitRequest request);

  BankResponse transfer(TransferRequest request);
}
