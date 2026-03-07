package com.abraham_bankole.runestone_bank.transaction.controller;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest;
import com.abraham_bankole.runestone_bank.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction APIs")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @Operation(summary = "Credit Account", description = "Credit an account with a specific amount")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @PostMapping("/credit")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public BankResponse credit(@RequestBody CreditDebitRequest creditRequest) {
    return transactionService.creditAccount(creditRequest);
  }

  @Operation(summary = "Debit Account", description = "Debit an account with a specific amount")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @PostMapping("/debit")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public BankResponse debit(@RequestBody CreditDebitRequest creditRequest) {
    return transactionService.debitAccount(creditRequest);
  }

  @Operation(summary = "Transfer Funds", description = "Transfer funds from one account to another")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @PostMapping("/transfer")
  @PreAuthorize("hasRole('ROLE_ADMIN') or #transferRequest.sender == authentication.principal.accountNumber")
  public BankResponse transfer(@RequestBody TransferRequest transferRequest) {
    return transactionService.transfer(transferRequest);
  }

  @Operation(summary = "Transaction History", description = "Get transaction history for an account")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping("/history")
  @PreAuthorize("hasRole('ROLE_ADMIN') or #accountNumber == authentication.principal.accountNumber")
  public List<Transaction> getHistory(@RequestParam String accountNumber) {
    return transactionService.getTransactionHistory(accountNumber);
  }
}
