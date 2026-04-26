package com.abraham_bankole.runestone_bank.user.dto;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
  private String email;
  private String password;

  public BankResponse login(LoginDto loginDto) {
    Authentication authentication = null;
    authentication =
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
    return null;
  }
}
