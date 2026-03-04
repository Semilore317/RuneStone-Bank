package com.abraham_bankole.runestone_bank.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.abraham_bankole.runestone_bank.user.dto.AccountInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {
  private String responseCode;
  private String responseMessage;
  private AccountInfo accountInfo;
  private String jwt;
}
