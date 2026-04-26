package com.abraham_bankole.runestone_bank.security.repository;

import com.abraham_bankole.runestone_bank.security.entity.TokenBlacklist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
  Optional<TokenBlacklist> findByToken(String token);
}
