package com.abraham_bankole.runestone_bank.transaction.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.service.UserAccountService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

  @Mock private UserAccountService userAccountService;

  @Mock private TransactionRepository transactionRepository;

  @Mock private OutboxService outboxService;

  @InjectMocks private TransactionServiceImpl transactionService;

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

  @Test
  void testDebitAccount_WhenAccountDoesNotExist_ReturnsError() {
    CreditDebitRequest request = new CreditDebitRequest("999999", BigDecimal.valueOf(100));
    when(userAccountService.accountExists("999999")).thenReturn(false);
    BankResponse response = transactionService.debitAccount(request);
    assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, response.getResponseCode());
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void testDebitAccount_WhenInsufficientBalance_ReturnsError() {
    CreditDebitRequest request = new CreditDebitRequest("123456", BigDecimal.valueOf(500));
    when(userAccountService.accountExists("123456")).thenReturn(true);
    when(userAccountService.getBalance("123456")).thenReturn(BigDecimal.valueOf(100)); // 100 < 500
    BankResponse response = transactionService.debitAccount(request);
    assertEquals(AccountUtils.INSUFFICIENT_BALANCE_CODE, response.getResponseCode());
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void testDebitAccount_Success() {
    CreditDebitRequest request = new CreditDebitRequest("123456", BigDecimal.valueOf(100));
    when(userAccountService.accountExists("123456")).thenReturn(true);
    when(userAccountService.getBalance("123456")).thenReturn(BigDecimal.valueOf(500));
    when(userAccountService.getFullName("123456")).thenReturn("John Doe");

    BankResponse response = transactionService.debitAccount(request);

    assertEquals(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE, response.getResponseCode());
    assertEquals(BigDecimal.valueOf(400), response.getAccountInfo().getAccountBalance());
    verify(userAccountService, times(1)).updateBalance("123456", BigDecimal.valueOf(400));
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void testTransfer_WhenReceiverDoesNotExist_ReturnsError() {
    com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest request =
        com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest.builder()
            .sender("111")
            .receiver("222")
            .amount(BigDecimal.valueOf(100))
            .build();
    when(userAccountService.accountExists("222")).thenReturn(false);
    BankResponse response = transactionService.transfer(request);
    assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, response.getResponseCode());
  }

  @Test
  void testTransfer_WhenInsufficientBalance_ReturnsError() {
    com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest request =
        com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest.builder()
            .sender("111")
            .receiver("222")
            .amount(BigDecimal.valueOf(500))
            .build();
    when(userAccountService.accountExists("222")).thenReturn(true);
    when(userAccountService.getBalance("111")).thenReturn(BigDecimal.valueOf(100));
    BankResponse response = transactionService.transfer(request);
    assertEquals(AccountUtils.INSUFFICIENT_BALANCE_CODE, response.getResponseCode());
  }

  @Test
  void testTransfer_Success() {
    com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest request =
        com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest.builder()
            .sender("111")
            .receiver("222")
            .amount(BigDecimal.valueOf(100))
            .build();

    when(userAccountService.accountExists("222")).thenReturn(true);
    when(userAccountService.getBalance("111")).thenReturn(BigDecimal.valueOf(500));
    when(userAccountService.getBalance("222")).thenReturn(BigDecimal.valueOf(100));
    when(userAccountService.getFullName("111")).thenReturn("Sender Name");
    when(userAccountService.getEmail("111")).thenReturn("sender@test.com");
    when(userAccountService.getFullName("222")).thenReturn("Receiver Name");
    when(userAccountService.getEmail("222")).thenReturn("receiver@test.com");

    BankResponse response = transactionService.transfer(request);

    assertEquals(AccountUtils.TRANSACTION_SUCCESSFUL_CODE, response.getResponseCode());
    verify(userAccountService, times(1)).updateBalance("111", BigDecimal.valueOf(400));
    verify(userAccountService, times(1)).updateBalance("222", BigDecimal.valueOf(200));
    verify(transactionRepository, times(2)).save(any(Transaction.class));
    verify(outboxService, times(1)).exportEvent(anyString(), anyString(), anyString(), any());
  }
}
