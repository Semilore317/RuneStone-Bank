package com.abraham_bankole.runestone_bank.common.service;

import java.math.BigDecimal;

/**
 * Abstraction over user-account operations so that other domains (transaction, statement) don't
 * depend on user.repository directly.
 */
public interface UserAccountService {

  boolean accountExists(String accountNumber);

  BigDecimal getBalance(String accountNumber);

  String getFullName(String accountNumber);

  String getEmail(String accountNumber);

  String getAddress(String accountNumber);

  /** Atomically set the account balance to the given value. */
  void updateBalance(String accountNumber, BigDecimal newBalance);
}
