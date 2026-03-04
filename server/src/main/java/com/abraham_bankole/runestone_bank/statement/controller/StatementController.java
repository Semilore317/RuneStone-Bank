package com.abraham_bankole.runestone_bank.statement.controller;

import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.statement.service.BankStatementService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankstatement")
@AllArgsConstructor
@Tag(name = "Bank Statement APIs")
public class StatementController {

  private BankStatementService bankStatement;

  @Operation(
      summary = "Generate Bank Statement",
      description = "Generate and email bank statement for a given account and date range")
  @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
  @GetMapping
  public List<Transaction> generateBankStatement(
      @RequestParam String accountNumber, @RequestParam String start, @RequestParam String end)
      throws DocumentException, FileNotFoundException, MessagingException {
    return bankStatement.generateStatement(accountNumber, start, end);
  }
}
