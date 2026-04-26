package com.abraham_bankole.runestone_bank.security.config;

import com.abraham_bankole.runestone_bank.security.entity.TokenBlacklist;
import com.abraham_bankole.runestone_bank.security.repository.TokenBlacklistRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final TokenBlacklistRepository tokenBlacklistRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      TokenBlacklist blacklistedToken = new TokenBlacklist();
      blacklistedToken.setToken(token);
      tokenBlacklistRepository.save(blacklistedToken);
    }
  }
}
