package com.abraham_bankole.runestone_bank.transaction.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionDto {
  private String transactionType;
  private BigDecimal amount;
  private String accountNumber;
  private String status;
}
