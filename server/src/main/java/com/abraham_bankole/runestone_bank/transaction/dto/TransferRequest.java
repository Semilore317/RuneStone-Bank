package com.abraham_bankole.runestone_bank.transaction.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
