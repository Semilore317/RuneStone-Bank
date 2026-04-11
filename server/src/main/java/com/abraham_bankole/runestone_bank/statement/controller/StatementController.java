package com.abraham_bankole.runestone_bank.statement.controller;

import com.abraham_bankole.runestone_bank.statement.service.BankStatementService;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bankstatement")
@AllArgsConstructor
@Tag(name = "Bank Statement APIs")
public class StatementController {

  private final BankStatementService bankStatement;

  @Operation(
      summary = "Generate Bank Statement",
      description = "Generate and email bank statement for a given account and date range")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public List<Transaction> generateBankStatement(
      @RequestParam String accountNumber, @RequestParam String start, @RequestParam String end)
      throws DocumentException, FileNotFoundException {
    return bankStatement.generateStatement(accountNumber, start, end);
  }

  @Operation(
      summary = "Request Bank Statement via Email",
      description = "Request a bank statement to be sent to the account's email address.")
  @ApiResponse(responseCode = "200", description = "Request processed successfully")
  @PostMapping("/email")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #accountNumber.equals(principal.accountNumber)")
  public ResponseEntity<String> requestStatementByEmail(
      @RequestParam String accountNumber, @RequestParam String start, @RequestParam String end)
      throws DocumentException, FileNotFoundException {
    bankStatement.generateStatement(accountNumber, start, end);
    return ResponseEntity.ok(
        "Statement request processed. You will receive the statement via email shortly.");
  }
}
