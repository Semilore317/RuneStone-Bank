package com.abraham_bankole.runestone_bank.user.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.user.dto.EnquiryRequest;
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

    @Test
    void testCreateAccount_Success() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("John").lastName("Doe")
                .email("john@example.com").password("password")
                .phoneNumber("1234567890").build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded_pass");

        User savedUser = User.builder()
                .id(1L)
                .firstName("John").lastName("Doe")
                .email("john@example.com")
                .accountNumber("123456789")
                .accountBalance(java.math.BigDecimal.ZERO)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        BankResponse response = userService.createAccount(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE, response.getResponseCode());
        verify(userRepository, times(1)).save(any(User.class));
        verify(outboxService, times(1)).exportEvent(anyString(), anyString(), anyString(), any());
    }

    @Test
    void testBalanceEnquiry_WhenAccountExists_ReturnsBalance() {
        // Arrange
        EnquiryRequest request = EnquiryRequest.builder().accountNumber("123456").build();
        User testUser = User.builder()
                .accountNumber("123456").accountBalance(java.math.BigDecimal.valueOf(100.50))
                .firstName("John").lastName("Doe").build();

        when(userRepository.existsByAccountNumber("123456")).thenReturn(true);
        when(userRepository.findByAccountNumber("123456")).thenReturn(testUser);

        // Act
        BankResponse response = userService.balanceEnquiry(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_FOUND_CODE, response.getResponseCode());
        assertEquals(java.math.BigDecimal.valueOf(100.50), response.getAccountInfo().getAccountBalance());
    }

    @Test
    void testBalanceEnquiry_WhenAccountDoesNotExist_ReturnsError() {
        // Arrange
        EnquiryRequest request = EnquiryRequest.builder().accountNumber("999999").build();
        when(userRepository.existsByAccountNumber("999999")).thenReturn(false);

        // Act
        BankResponse response = userService.balanceEnquiry(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, response.getResponseCode());
    }
}
