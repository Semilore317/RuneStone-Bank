package com.abraham_bankole.runestone_bank.statement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.abraham_bankole.runestone_bank.common.service.UserAccountService;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import com.abraham_bankole.runestone_bank.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class BankStatementServiceTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private UserAccountService userAccountService;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks private BankStatementService bankStatementService;

  @Test
  void testGenerateStatement_Success() throws Exception {
    String accountNumber = "123456";
    String start = "2023-01-01";
    String end = "2023-12-31";

    when(userAccountService.getFullName(accountNumber)).thenReturn("John Doe");
    when(userAccountService.getAddress(accountNumber)).thenReturn("123 Main St");
    when(userAccountService.getEmail(accountNumber)).thenReturn("john@test.com");

    Transaction t1 =
        Transaction.builder()
            .accountNumber(accountNumber)
            .transactionType("CREDIT")
            .amount(BigDecimal.TEN)
            .timeOfCreation(LocalDateTime.now())
            .status("SUCCESS")
            .build();

    List<Transaction> mockTransactions = Collections.singletonList(t1);

    when(transactionRepository.findByAccountNumberAndTimeOfCreationBetween(
            eq(accountNumber), any(), any()))
        .thenReturn(mockTransactions);

    List<Transaction> result = bankStatementService.generateStatement(accountNumber, start, end);

    assertEquals(1, result.size());
    verify(kafkaTemplate, times(1))
        .send(
            eq(com.abraham_bankole.runestone_bank.common.kafka.KafkaTopics.STATEMENT_READY), any());
  }
}
