package com.abraham_bankole.runestone_bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    // acct numbers
    private String sender;
    private String receiver;

    private BigDecimal amount;
}
