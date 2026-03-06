package com.abraham_bankole.runestone_bank.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
  @Schema(name = "User Account Name")
  private String accountName;

  @Schema(name = "User Account Balance")
  private BigDecimal accountBalance;

  @Schema(name = "User Account Number")
  private String accountNumber;
}
