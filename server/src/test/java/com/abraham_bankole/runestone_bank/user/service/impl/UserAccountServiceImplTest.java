package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @Test
    void testAccountExists_ReturnsTrue() {
        when(userRepository.existsByAccountNumber("123")).thenReturn(true);
        assertTrue(userAccountService.accountExists("123"));
    }

    @Test
    void testGetBalance_ReturnsBalance() {
        User user = new User();
        user.setAccountBalance(BigDecimal.TEN);
        when(userRepository.findByAccountNumber("123")).thenReturn(user);

        assertEquals(BigDecimal.TEN, userAccountService.getBalance("123"));
    }

    @Test
    void testUpdateBalance_SavesNewBalance() {
        User user = new User();
        user.setAccountBalance(BigDecimal.TEN);
        when(userRepository.findByAccountNumber("123")).thenReturn(user);

        userAccountService.updateBalance("123", BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(50), user.getAccountBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindUser_WhenNotFound_ThrowsException() {
        when(userRepository.findByAccountNumber("999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userAccountService.getBalance("999"));
            
        assertEquals("Account not found: 999", exception.getMessage());
    }
}
