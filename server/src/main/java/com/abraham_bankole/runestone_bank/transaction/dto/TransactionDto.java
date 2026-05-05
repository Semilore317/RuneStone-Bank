package com.abraham_bankole.runestone_bank.transaction.dto;

import java.math.BigDecimal;

import com.abraham_bankole.runestone_bank.common.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionDto {
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;
  private BigDecimal amount;
  private String accountNumber;
  private String status;
  private String counterpartyAccountNumber;
  private String counterpartyName;
}
