package com.abraham_bankole.runestone_bank.transaction.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDebitRequest {
  private String accountNumber;
  private BigDecimal amount;
}
