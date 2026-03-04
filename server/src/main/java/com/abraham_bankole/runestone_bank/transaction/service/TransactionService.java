package com.abraham_bankole.runestone_bank.transaction.service;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.dto.TransactionDto;
import com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest;

public interface TransactionService {
  void saveTransaction(TransactionDto transaction);

  BankResponse creditAccount(CreditDebitRequest request);

  BankResponse debitAccount(CreditDebitRequest request);

  BankResponse transfer(TransferRequest request);
}
