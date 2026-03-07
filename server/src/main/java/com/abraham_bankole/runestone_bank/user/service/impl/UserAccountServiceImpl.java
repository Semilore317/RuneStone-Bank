package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.common.service.UserAccountService;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implements the common UserAccountService contract using the user domain's
 * repository — keeps UserRepository encapsulated within the user domain.
 */
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;

    @Override
    public boolean accountExists(String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return findUser(accountNumber).getAccountBalance();
    }

    @Override
    public String getFullName(String accountNumber) {
        User user = findUser(accountNumber);
        return user.getFullName();
    }

    @Override
    public String getEmail(String accountNumber) {
        return findUser(accountNumber).getEmail();
    }

    @Override
    public String getAddress(String accountNumber) {
        return findUser(accountNumber).getAddress();
    }

    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) {
        User user = findUser(accountNumber);
        user.setAccountBalance(newBalance);
        userRepository.save(user);
    }

    private User findUser(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber);
        if (user == null) {
            throw new RuntimeException("Account not found: " + accountNumber);
        }
        return user;
    }
}
