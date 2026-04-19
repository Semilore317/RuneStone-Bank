package com.abraham_bankole.runestone_bank.transaction.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.service.UserAccountService;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testCreditAccount_WhenAccountDoesNotExist_ReturnsError() {
        // Arrange
        CreditDebitRequest request = new CreditDebitRequest("999999", BigDecimal.valueOf(100));
                
        when(userAccountService.accountExists("999999")).thenReturn(false);

        // Act
        BankResponse response = transactionService.creditAccount(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, response.getResponseCode());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreditAccount_Success() {
        // Arrange
        CreditDebitRequest request = new CreditDebitRequest("123456", BigDecimal.valueOf(100));

        when(userAccountService.accountExists("123456")).thenReturn(true);
        when(userAccountService.getBalance("123456")).thenReturn(BigDecimal.valueOf(500));
        when(userAccountService.getFullName("123456")).thenReturn("John Doe");

        // Act
        BankResponse response = transactionService.creditAccount(request);

        // Assert
        assertEquals(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE, response.getResponseCode());
        assertEquals(BigDecimal.valueOf(600), response.getAccountInfo().getAccountBalance());
        verify(userAccountService, times(1)).updateBalance("123456", BigDecimal.valueOf(600));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
