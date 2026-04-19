package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.user.dto.UserRequest;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateAccount_WhenEmailExists_ReturnsErrorResponse() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setEmail("john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act
        BankResponse response = userService.createAccount(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_EXISTS_CODE, response.getResponseCode());
        verify(userRepository, never()).save(any(User.class));
    }
}
