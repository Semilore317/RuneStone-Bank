package com.abraham_bankole.runestone_bank.controller;

import com.abraham_bankole.runestone_bank.dto.BankResponse;
import com.abraham_bankole.runestone_bank.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.dto.TransferRequest;
import com.abraham_bankole.runestone_bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction APIs")
public class TransactionController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Credit Account", description = "Credit an account with a specific amount")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/credit")
    public BankResponse credit(@RequestBody CreditDebitRequest creditRequest) {
        return userService.creditAccount(creditRequest);
    }

    @Operation(summary = "Debit Account", description = "Debit an account with a specific amount")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/debit")
    public BankResponse debit(@RequestBody CreditDebitRequest creditRequest) {
        return userService.debitAccount(creditRequest);
    }

    @Operation(summary = "Transfer Funds", description = "Transfer funds from one account to another")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest) {
        return userService.transfer(transferRequest);
    }
}
