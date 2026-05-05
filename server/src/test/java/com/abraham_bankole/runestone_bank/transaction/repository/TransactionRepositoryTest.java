package com.abraham_bankole.runestone_bank.transaction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.abraham_bankole.runestone_bank.common.enums.TransactionStatus;
import com.abraham_bankole.runestone_bank.common.enums.TransactionType;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TransactionRepositoryTest {

  @Autowired private TransactionRepository transactionRepository;

  @Test
  void testFindByAccountNumberOrderByTimeOfCreationDesc() {
    Transaction t1 =
        Transaction.builder()
            .accountNumber("123")
            .transactionType(TransactionType.CREDIT)
            .amount(BigDecimal.TEN)
            .timeOfCreation(LocalDateTime.now().minusDays(2))
            .status(TransactionStatus.SUCCESS)
            .build();

    Transaction t2 =
        Transaction.builder()
            .accountNumber("123")
            .transactionType(TransactionType.DEBIT)
            .amount(BigDecimal.ONE)
            .timeOfCreation(LocalDateTime.now())
            .status(TransactionStatus.SUCCESS)
            .build();

    transactionRepository.save(t1);
    transactionRepository.save(t2);

    List<Transaction> results =
        transactionRepository.findByAccountNumberOrderByTimeOfCreationDesc("123");

    assertEquals(2, results.size());
    assertEquals(TransactionType.DEBIT, results.get(0).getTransactionType()); // Newest first
  }

  @Test
  void testFindByAccountNumberAndTimeOfCreationBetween() {
    Transaction t1 = Transaction.builder().accountNumber("444").amount(BigDecimal.TEN).build();

    Transaction t2 = Transaction.builder().accountNumber("444").amount(BigDecimal.TEN).build();

    transactionRepository.save(t1);
    transactionRepository.save(t2);

    LocalDateTime now = LocalDateTime.now();

    List<Transaction> results =
        transactionRepository.findByAccountNumberAndTimeOfCreationBetween(
            "444", now.minusMinutes(5), now.plusMinutes(5));

    assertEquals(2, results.size());
  }
}
